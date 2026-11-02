"""Publish the lab's measured results without credentials or account details."""

import json
import os
from pathlib import Path
import time
import urllib.parse
import urllib.request


def get(url):
    if urllib.parse.urlparse(url).hostname != "sonarcloud.io":
        raise RuntimeError("Unexpected SonarCloud result host")
    req = urllib.request.Request(url, headers={
        "Authorization": "Bearer " + os.environ["SONAR_TOKEN"],
    })
    with urllib.request.urlopen(req, timeout=30) as response:
        return json.load(response)


def notice(title, value):
    message = json.dumps(value, ensure_ascii=True, separators=(",", ":"))
    message = message.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
    print(f"::notice title={title}::{message}")


metadata = dict(line.split("=", 1) for line in
                Path("gestion_bib/.scannerwork/report-task.txt").read_text().splitlines()
                if "=" in line)
for attempt in range(60):
    task = get(metadata["ceTaskUrl"])["task"]
    if task["status"] == "SUCCESS":
        break
    if task["status"] in {"FAILED", "CANCELED"}:
        raise RuntimeError("SonarCloud report processing: " + task["status"])
    time.sleep(5)
else:
    raise TimeoutError("SonarCloud processing did not finish within five minutes")

project = urllib.parse.quote(metadata["projectKey"], safe="")
metrics = ("bugs,vulnerabilities,code_smells,ncloc,duplicated_lines_density,coverage,"
           "complexity,cognitive_complexity,sqale_index,security_hotspots")
measures = get(f"https://sonarcloud.io/api/measures/component?component={project}&metricKeys={metrics}")
notice("TP SonarCloud measures", {item["metric"]: item.get("value")
                                 for item in measures["component"]["measures"]})
gate = get("https://sonarcloud.io/api/qualitygates/project_status?analysisId=" + task["analysisId"])
notice("TP SonarCloud quality gate", gate["projectStatus"])
issues = get(f"https://sonarcloud.io/api/issues/search?componentKeys={project}&resolved=false&ps=100")
notice("TP SonarCloud issues", [{key: issue.get(key) for key in
                                ("rule", "type", "severity", "component", "line", "message")}
                               for issue in issues["issues"]])

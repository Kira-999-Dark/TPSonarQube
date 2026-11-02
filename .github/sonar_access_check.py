"""Report access status without logging credentials or response contents."""

import json
import os
import urllib.error
import urllib.request


def request(url, token):
    req = urllib.request.Request(url, headers={
        "Authorization": "Bearer " + token,
        "User-Agent": "TPSonarQube-CI-access-check",
    })
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            return response.status, response.read()
    except urllib.error.HTTPError as error:
        return error.code, error.read()
    except (urllib.error.URLError, TimeoutError):
        return 0, b""


token = os.environ.get("SONAR_TOKEN", "")
if not token:
    raise SystemExit("::error title=SonarCloud access diagnosis::SONAR_TOKEN is missing")

auth_status, auth_body = request(
    "https://sonarcloud.io/api/authentication/validate", token)
try:
    valid = json.loads(auth_body).get("valid")
except (ValueError, AttributeError):
    valid = None

engine_status, engine_body = request(
    "https://api.sonarcloud.io/analysis/engine", token)
engine_text = engine_body.decode("utf-8", errors="replace").lower()
if "explicit deny" in engine_text:
    response_kind = "explicit-deny"
elif "<html" in engine_text or "<!doctype html" in engine_text:
    response_kind = "html"
else:
    response_kind = "other"

print("::notice title=SonarCloud access diagnosis::"
      f"authentication HTTP={auth_status}, valid={valid}; "
      f"engine HTTP={engine_status}, response={response_kind}")

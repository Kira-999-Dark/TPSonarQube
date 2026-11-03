"""Use an existing built-in Java profile for the university comparison."""

import json
import os
import urllib.error
import urllib.parse
import urllib.request

base = "https://sonarcloud.io/api/qualityprofiles/"
organization = "kira-999-dark"
project = "kira-999-Dark_TPSonarQube"
headers = {"Authorization": "Bearer " + os.environ["SONAR_TOKEN"]}
query = urllib.parse.urlencode({"organization": organization, "language": "java"})
with urllib.request.urlopen(urllib.request.Request(base + "search?" + query, headers=headers),
                            timeout=30) as response:
    profiles = json.load(response)["profiles"]
available = {profile["name"]: profile for profile in profiles}
print("::notice title=Available Java profiles::" + json.dumps(
    {name: profile.get("activeRuleCount") for name, profile in available.items()}))
target = next((name for name in ("Sonar way extended", "Sonar way comprehensive", "Sonar way")
               if name in available), None)
if target:
    form = urllib.parse.urlencode({"organization": organization, "project": project,
                                   "language": "java", "qualityProfile": target}).encode()
    try:
        with urllib.request.urlopen(urllib.request.Request(base + "add_project", data=form,
                                    headers=headers), timeout=30):
            print("::notice title=Selected Java profile::" + target)
    except urllib.error.HTTPError as error:
        print("::notice title=Java profile selection::Existing profile retained; HTTP="
              + str(error.code))
else:
    print("::notice title=Java profile selection::Existing built-in profile retained")

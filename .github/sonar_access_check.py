"""Report access status without logging credentials or response contents."""

import base64
import json
import os
import urllib.error
import urllib.request


def request(url, token, basic=False):
    try:
        authorization = ("Basic " + base64.b64encode((token + ":").encode()).decode()
                         if basic else "Bearer " + token)
        req = urllib.request.Request(url, headers={
            "Authorization": authorization,
            "User-Agent": "TPSonarQube-CI-access-check",
        })
        with urllib.request.urlopen(req, timeout=30) as response:
            return response.status, response.read()
    except urllib.error.HTTPError as error:
        return error.code, error.read()
    except (urllib.error.URLError, TimeoutError, ValueError, UnicodeError):
        return 0, b""


token = os.environ.get("SONAR_TOKEN", "")
if not token:
    raise SystemExit("::error title=SonarCloud access diagnosis::SONAR_TOKEN is missing")

normalized = token.strip()
for prefix in ("SONAR_TOKEN=", "Bearer "):
    if normalized.startswith(prefix):
        normalized = normalized[len(prefix):].strip()
if len(normalized) >= 2 and normalized[0] == normalized[-1] and normalized[0] in "\"'":
    normalized = normalized[1:-1].strip()

placeholder = normalized in {"SONAR_TOKEN", "github-actions-tp", "***"}
print("::notice title=SonarCloud credential format::"
      f"formatting_cleanup_needed={normalized != token}; "
      f"value_is_name_or_placeholder={placeholder}")

accepted = False
for label, candidate in [("stored", token)] + (
        [("normalized", normalized)] if normalized != token else []):
    validations = []
    for basic in (False, True):
        auth_status, auth_body = request(
            "https://sonarcloud.io/api/authentication/validate", candidate, basic)
        try:
            valid = json.loads(auth_body).get("valid")
        except (ValueError, AttributeError):
            valid = None
        validations.append(valid)
        method = "basic" if basic else "bearer"
        print(f"::notice title=SonarCloud {label} authentication::"
              f"method={method}, HTTP={auth_status}, valid={valid}")
    if any(value is True for value in validations):
        accepted = True
        if label == "normalized" and "\n" not in candidate and "\r" not in candidate:
            # GitHub consumes this masking command without displaying its value.
            print("::add-mask::" + candidate)
            with open(os.environ["GITHUB_ENV"], "a", encoding="utf-8") as env_file:
                env_file.write("SONAR_TOKEN=" + candidate + "\n")
            token = candidate
        break

if not accepted:
    raise SystemExit("::error title=SonarCloud authentication result::"
                     "Stored credential rejected by both authentication methods; "
                     "no token value has been logged.")

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
      f"engine HTTP={engine_status}, response={response_kind}")

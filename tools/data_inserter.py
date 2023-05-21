import http.client
import json

ll = [x for x in range(1, 170)]

conn = http.client.HTTPConnection("158.160.16.111:8000")
headers = {
  'Content-Type': 'application/json'
}
for x in ll:
    payload = json.dumps({
        "username": f"dummy{x}",
        "server": "158.160.16.111",
        "public_key": "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALStFwM+GNA1f2ng42EnvOZpgLHHYmzibGNd8J1P1hwdeJSKnBmiWBMIc5BI2EGi6s0YXudTLlsSDVfAE9x8FS8CAwEAAQ=="
    })
    conn.request("POST", "/users/register/once", payload, headers)
    res = conn.getresponse()
    data = res.read()
    print(data.decode("utf-8"))

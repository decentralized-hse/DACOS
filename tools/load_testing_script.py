import time
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed, wait
from tqdm import tqdm

url = "http://158.160.16.111:80/users"
num_requests = 10000
num_concurrent_requests = 100


def make_request(url):
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.status_code
    except requests.exceptions.RequestException as e:
        # Handle request errors here, such as logging the error
        return None


def load_test(url: str, num_requests: int, num_concurrent_requests: int):
    with ThreadPoolExecutor(max_workers=num_concurrent_requests) as executor:
        futures = [executor.submit(make_request, url) for _ in range(num_requests)]
        status_codes = list(tqdm(as_completed(futures), total=num_requests))

    success_count = sum(1 for code in status_codes if code.result() == 200)
    print(f"\nSuccessful requests: {success_count}/{num_requests}")
    print(f"Success rate: {success_count / num_requests * 100:.2f}%")


if __name__ == "__main__":
    print(f"Starting load test: {num_requests} requests on {url}")
    start_time = time.time()
    load_test(url, num_requests, num_concurrent_requests)
    end_time = time.time()
    print(f"Load test finished in {end_time - start_time:.2f} seconds")

import json
import pika
import random
import redis
import signal
import sys
import time
from haversine import haversine, Unit

region = None
redis_client = None


def match_trip_to_driver(ch, method, properties, body):
    start_time = time.time()

    trip_request = json.loads(body.decode())

    pickup_location = trip_request["pickup"]
    pickup_longitude = pickup_location["longitude"]
    pickup_latitude = pickup_location["latitude"]
    trip_id = trip_request["id"]
    drivers_hash = f"drivers:{region}"

    drivers = redis_client.hgetall(drivers_hash)

    if not drivers:
        print("Could not match trip, due to no active drivers in system")

    drivers = {driver_id.decode(): tuple(map(float, location.decode().split())) for driver_id, location in drivers.items()}
    pickup_location = (pickup_latitude, pickup_longitude)

    closest_driver_id = None
    min_distance = float("inf")

    while drivers:
        for driver_id, driver_location in drivers.items():
            distance = haversine(driver_location, pickup_location, unit=Unit.KILOMETERS)
            if distance < min_distance:
                min_distance = distance
                closest_driver_id = driver_id

        result = redis_client.hdel(drivers_hash, closest_driver_id)

        if result == 1:
            print(f"Driver with ID {closest_driver_id} removed from Redis, in preparation for matching")
            break
        else:
            print(f"Attempted to match Driver with ID {closest_driver_id}, "
                  f"but they were already matched, restarting process")
            closest_driver_id = None
            min_distance = float("inf")

            drivers = redis_client.hgetall(drivers_hash)
            drivers = {driver_id.decode(): tuple(map(float, coords.decode().split())) for driver_id, coords in
                       drivers.items()}
            if not drivers:
                print("Could not match trip, due to no active drivers in system")

    if region == "at_linz":
        time.sleep(random.randint(1, 2))
    elif region == "at_vienna":
        time.sleep(random.randint(3, 5))
    elif region == "de_berlin":
        time.sleep(random.randint(8, 11))

    end_time = time.time()
    processing_time = (end_time - start_time) * 1000

    result_json = json.dumps({
        "requestId": trip_id,
        "processingTime": processing_time if closest_driver_id else 0,
        "driverId": closest_driver_id if closest_driver_id else ""
    }).encode("utf-8")

    channel.basic_publish(exchange="dst.workers", routing_key="requests." + region, body=result_json)
    if closest_driver_id:
        print(f"Successfully matched trip request {trip_id} for region: {region} "
              f"to driver {closest_driver_id} in {processing_time}ms")
    else:
        print(f"Could not match trip request {trip_id} for region: {region} to a driver")


def sigterm_handler(signum, frame):
    print("Shutting worker down.")
    sys.exit(0)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: worker <REGION>")
        sys.exit(1)

    signal.signal(signal.SIGTERM, sigterm_handler)

    region = sys.argv[1]
    print("Registering worker to region: ", region)

    redis_client = redis.Redis(host="redis")

    credentials = pika.PlainCredentials("dst", "dst")
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host="rabbit", port=5672, credentials=credentials))

    channel = connection.channel()
    worker_queue = "dst." + region

    channel.queue_declare(queue=worker_queue, durable=True)
    channel.basic_consume(queue=worker_queue, on_message_callback=match_trip_to_driver)

    print("Starting to consume queue: ", worker_queue)
    channel.start_consuming()

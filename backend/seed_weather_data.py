import psycopg2
import random
import datetime
from tqdm import tqdm
import csv
import io
import time
import argparse

# Weather status options
WEATHER_STATUSES = [
    "Sunny", "Partly Cloudy", "Cloudy", "Overcast",
    "Light Rain", "Moderate Rain", "Heavy Rain", "Thunderstorm",
    "Snow", "Sleet", "Freezing Rain", "Foggy", "Misty",
    "Windy", "Clear", "Hazy", "Stormy", "Drizzle",
    "Hot", "Cold", "Warm", "Cool", "Humid", "Dry"
]

# For more realistic temperature ranges based on climate types
CLIMATE_TYPES = {
    "Tropical": {"min_temp_range": (15, 25), "max_temp_range": (25, 40)},
    "Desert": {"min_temp_range": (0, 20), "max_temp_range": (25, 45)},
    "Mediterranean": {"min_temp_range": (5, 15), "max_temp_range": (15, 35)},
    "Continental": {"min_temp_range": (-20, 10), "max_temp_range": (10, 30)},
    "Polar": {"min_temp_range": (-40, -10), "max_temp_range": (-10, 10)},
    "Temperate": {"min_temp_range": (0, 15), "max_temp_range": (15, 25)}
}

def create_schema(conn):
    """Create database schema if it doesn't exist"""
    cursor = conn.cursor()
    print("Creating schema if it doesn't exist...")

    # Create locations table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS locations (
        code VARCHAR(12) PRIMARY KEY,
        city_name VARCHAR(128) NOT NULL,
        region_name VARCHAR(128) NOT NULL,
        country_name VARCHAR(64) NOT NULL,
        country_code VARCHAR(2) NOT NULL,
        enabled BOOLEAN NOT NULL,
        trashed BOOLEAN NOT NULL
    )
    """)

    # Create realtime_weather table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS realtime_weather (
        location_code VARCHAR(12) PRIMARY KEY REFERENCES locations(code),
        temperature INTEGER NOT NULL,
        humidity INTEGER NOT NULL,
        precipitation INTEGER NOT NULL,
        wind_speed INTEGER NOT NULL,
        status VARCHAR(50) NOT NULL,
        last_updated TIMESTAMP NOT NULL
    )
    """)

    # Create weather_daily table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS weather_daily (
        day_of_month INTEGER NOT NULL,
        month INTEGER NOT NULL,
        location_code VARCHAR(12) NOT NULL REFERENCES locations(code),
        min_temp INTEGER NOT NULL,
        max_temp INTEGER NOT NULL,
        precipitation INTEGER NOT NULL,
        status VARCHAR(50) NOT NULL,
        PRIMARY KEY (day_of_month, month, location_code)
    )
    """)

    # Create weather_hourly table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS weather_hourly (
        hour_of_day INTEGER NOT NULL,
        location_code VARCHAR(12) NOT NULL REFERENCES locations(code),
        temperature INTEGER NOT NULL,
        precipitation INTEGER NOT NULL,
        status VARCHAR(50) NOT NULL,
        PRIMARY KEY (hour_of_day, location_code)
    )
    """)

    # Create indexes for performance
    # cursor.execute("CREATE INDEX IF NOT EXISTS idx_location_city ON locations(city_name)")
    # cursor.execute("CREATE INDEX IF NOT EXISTS idx_location_country ON locations(country_name)")
    # cursor.execute("CREATE INDEX IF NOT EXISTS idx_location_region ON locations(region_name)")
    # cursor.execute("CREATE INDEX IF NOT EXISTS idx_daily_weather_month ON weather_daily(month)")
    # cursor.execute("CREATE INDEX IF NOT EXISTS idx_realtime_temp ON realtime_weather(temperature)")
    # cursor.execute("CREATE INDEX IF NOT EXISTS idx_hourly_temp ON weather_hourly(temperature)")

    conn.commit()
    cursor.close()
    print("Schema created successfully!")

def load_cities_data():
    """Return a list of city data"""
    # This is an embedded minimal dataset of 1000 cities
    # Format: [code, city_name, region_name, country_name, country_code, climate_type]
    cities_data = []

    # World cities data with climate types assigned
    # Only showing a few examples here - in the full script there would be 1000+ cities
    sample_cities = [
        ["NYC", "New York", "New York", "United States", "US", "Continental"],
        ["LON", "London", "England", "United Kingdom", "GB", "Temperate"],
        ["PAR", "Paris", "Île-de-France", "France", "FR", "Temperate"],
        ["TYO", "Tokyo", "Kanto", "Japan", "JP", "Temperate"],
        ["SYD", "Sydney", "New South Wales", "Australia", "AU", "Mediterranean"],
        ["CAI", "Cairo", "Cairo Governorate", "Egypt", "EG", "Desert"],
        ["RIO", "Rio de Janeiro", "Rio de Janeiro", "Brazil", "BR", "Tropical"],
        ["BJS", "Beijing", "Beijing", "China", "CN", "Continental"],
        ["MOS", "Moscow", "Moscow Oblast", "Russia", "RU", "Continental"],
        ["CPT", "Cape Town", "Western Cape", "South Africa", "ZA", "Mediterranean"],
    ]

    # In a real script, you would load hundreds or thousands of cities
    # This is just a small sample to demonstrate the pattern

    # Generate codes for a large number of additional cities
    for i in range(1, 2000):
        code = f"CT{i:04d}"
        city_name = f"City {i}"
        region_name = f"Region {random.randint(1, 100)}"
        country_name = f"Country {random.randint(1, 50)}"
        country_code = country_name[:2].upper()
        climate_type = random.choice(list(CLIMATE_TYPES.keys()))

        cities_data.append([code, city_name, region_name, country_name, country_code, climate_type])

    # Add the sample real cities at the beginning
    cities_data = sample_cities + cities_data

    return cities_data

def insert_locations(conn, cities_data):
    """Insert locations data"""
    cursor = conn.cursor()

    print("Inserting locations data...")

    # Use copy_from for faster bulk insert
    locations_data = io.StringIO()
    for city in cities_data:
        code, city_name, region_name, country_name, country_code, _ = city
        enabled = random.choice([True, False, True, True, True])  # 80% enabled
        trashed = not enabled if random.random() < 0.8 else random.choice([True, False])

        locations_data.write(f"{code}\t{city_name}\t{region_name}\t{country_name}\t{country_code}\t{enabled}\t{trashed}\n")

    locations_data.seek(0)

    try:
        cursor.copy_from(
            locations_data,
            'locations',
            columns=('code', 'city_name', 'region_name', 'country_name', 'country_code', 'enabled', 'trashed'),
            sep='\t'
        )
        conn.commit()
    except Exception as e:
        conn.rollback()
        print(f"Error inserting locations: {e}")
        # Fall back to regular inserts if COPY fails
        print("Falling back to regular inserts...")
        for city in tqdm(cities_data):
            code, city_name, region_name, country_name, country_code, _ = city
            enabled = random.choice([True, False, True, True, True])  # 80% enabled
            trashed = not enabled if random.random() < 0.8 else random.choice([True, False])

            cursor.execute(
                "INSERT INTO locations (code, city_name, region_name, country_name, country_code, enabled, trashed) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                (code, city_name, region_name, country_name, country_code, enabled, trashed)
            )
        conn.commit()

    cursor.close()
    print(f"Inserted {len(cities_data)} locations.")

def insert_realtime_weather(conn, cities_data):
    """Insert realtime weather data"""
    cursor = conn.cursor()

    print("Inserting realtime weather data...")

    # Use copy_from for faster bulk insert
    realtime_data = io.StringIO()

    now = datetime.datetime.now()

    for city in cities_data:
        code, _, _, _, _, climate_type = city

        climate = CLIMATE_TYPES[climate_type]
        temp_range = climate["max_temp_range"]

        temperature = random.randint(temp_range[0], temp_range[1])
        humidity = random.randint(30, 95)
        precipitation = random.randint(0, 100) if random.random() < 0.3 else 0
        wind_speed = random.randint(0, 80)
        status = random.choice(WEATHER_STATUSES)
        last_updated = now - datetime.timedelta(minutes=random.randint(0, 60))

        realtime_data.write(f"{code}\t{temperature}\t{humidity}\t{precipitation}\t{wind_speed}\t{status}\t{last_updated}\n")

    realtime_data.seek(0)

    try:
        cursor.copy_from(
            realtime_data,
            'realtime_weather',
            columns=('location_code', 'temperature', 'humidity', 'precipitation', 'wind_speed', 'status', 'last_updated'),
            sep='\t'
        )
        conn.commit()
    except Exception as e:
        conn.rollback()
        print(f"Error inserting realtime weather: {e}")
        # Fall back to regular inserts
        print("Falling back to regular inserts...")

        now = datetime.datetime.now()

        for city in tqdm(cities_data):
            code, _, _, _, _, climate_type = city

            climate = CLIMATE_TYPES[climate_type]
            temp_range = climate["max_temp_range"]

            temperature = random.randint(temp_range[0], temp_range[1])
            humidity = random.randint(30, 95)
            precipitation = random.randint(0, 100) if random.random() < 0.3 else 0
            wind_speed = random.randint(0, 80)
            status = random.choice(WEATHER_STATUSES)
            last_updated = now - datetime.timedelta(minutes=random.randint(0, 60))

            cursor.execute(
                "INSERT INTO realtime_weather (location_code, temperature, humidity, precipitation, wind_speed, status, last_updated) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                (code, temperature, humidity, precipitation, wind_speed, status, last_updated)
            )

        conn.commit()

    cursor.close()
    print(f"Inserted realtime weather data for {len(cities_data)} locations.")

def insert_daily_weather(conn, cities_data, months=12):
    """Insert daily weather forecasts for multiple months"""
    cursor = conn.cursor()

    print(f"Inserting daily weather data for {months} months...")

    # For each city, generate daily weather for each month
    daily_data = io.StringIO()

    total_records = 0

    for city in cities_data:
        code, _, _, _, _, climate_type = city
        climate = CLIMATE_TYPES[climate_type]

        for month in range(1, months + 1):
            # Determine days in month (simplified)
            days_in_month = 31 if month in [1, 3, 5, 7, 8, 10, 12] else 30
            if month == 2:
                days_in_month = 28

            for day in range(1, days_in_month + 1):
                # Adjust temperature ranges by month (northern hemisphere seasons)
                season_adjustment = 0
                if climate_type != "Tropical":
                    if month in [12, 1, 2]:  # Winter
                        season_adjustment = -10
                    elif month in [6, 7, 8]:  # Summer
                        season_adjustment = 10

                min_range = climate["min_temp_range"]
                max_range = climate["max_temp_range"]

                min_temp = random.randint(min_range[0], min_range[1]) + season_adjustment
                max_temp = random.randint(max_range[0], max_range[1]) + season_adjustment

                # Ensure max_temp > min_temp
                max_temp = max(max_temp, min_temp + random.randint(3, 10))

                precipitation = random.randint(0, 100) if random.random() < 0.3 else 0
                status = random.choice(WEATHER_STATUSES)

                daily_data.write(f"{day}\t{month}\t{code}\t{min_temp}\t{max_temp}\t{precipitation}\t{status}\n")
                total_records += 1

    daily_data.seek(0)

    try:
        cursor.copy_from(
            daily_data,
            'weather_daily',
            columns=('day_of_month', 'month', 'location_code', 'min_temp', 'max_temp', 'precipitation', 'status'),
            sep='\t'
        )
        conn.commit()
    except Exception as e:
        conn.rollback()
        print(f"Error bulk inserting daily weather: {e}")
        print("Daily weather data is too large for bulk insert, using batch inserts instead...")

        batch_size = 1000
        daily_values = []

        for city in tqdm(cities_data):
            code, _, _, _, _, climate_type = city
            climate = CLIMATE_TYPES[climate_type]

            for month in range(1, months + 1):
                # Determine days in month (simplified)
                days_in_month = 31 if month in [1, 3, 5, 7, 8, 10, 12] else 30
                if month == 2:
                    days_in_month = 28

                for day in range(1, days_in_month + 1):
                    # Adjust temperature ranges by month (northern hemisphere seasons)
                    season_adjustment = 0
                    if climate_type != "Tropical":
                        if month in [12, 1, 2]:  # Winter
                            season_adjustment = -10
                        elif month in [6, 7, 8]:  # Summer
                            season_adjustment = 10

                    min_range = climate["min_temp_range"]
                    max_range = climate["max_temp_range"]

                    min_temp = random.randint(min_range[0], min_range[1]) + season_adjustment
                    max_temp = random.randint(max_range[0], max_range[1]) + season_adjustment

                    # Ensure max_temp > min_temp
                    max_temp = max(max_temp, min_temp + random.randint(3, 10))

                    precipitation = random.randint(0, 100) if random.random() < 0.3 else 0
                    status = random.choice(WEATHER_STATUSES)

                    daily_values.append((day, month, code, min_temp, max_temp, precipitation, status))

                    # Execute in batches
                    if len(daily_values) >= batch_size:
                        args_str = ','.join(cursor.mogrify("(%s,%s,%s,%s,%s,%s,%s)", x).decode('utf-8') for x in daily_values)
                        cursor.execute("INSERT INTO weather_daily (day_of_month, month, location_code, min_temp, max_temp, precipitation, status) VALUES " + args_str)
                        conn.commit()
                        daily_values = []

        # Insert remaining values
        if daily_values:
            args_str = ','.join(cursor.mogrify("(%s,%s,%s,%s,%s,%s,%s)", x).decode('utf-8') for x in daily_values)
            cursor.execute("INSERT INTO weather_daily (day_of_month, month, location_code, min_temp, max_temp, precipitation, status) VALUES " + args_str)
            conn.commit()

    cursor.close()
    print(f"Inserted {total_records} daily weather records.")

def insert_hourly_weather(conn, cities_data):
    """Insert hourly weather forecasts"""
    cursor = conn.cursor()

    print("Inserting hourly weather data...")

    # For each city, generate 24 hours of weather data
    hourly_data = io.StringIO()

    for city in cities_data:
        code, _, _, _, _, climate_type = city
        climate = CLIMATE_TYPES[climate_type]

        # Generate a base temperature for the day
        base_temp = random.randint(
            climate["min_temp_range"][1],
            climate["max_temp_range"][0] + (climate["max_temp_range"][1] - climate["max_temp_range"][0]) // 2
        )

        # Generate weather for each hour
        for hour in range(24):
            # Temperature varies by hour (cooler at night, warmer during day)
            hour_adjustment = 0
            if 6 <= hour <= 18:  # Daytime
                hour_adjustment = random.randint(0, 10)
            else:  # Nighttime
                hour_adjustment = random.randint(-10, 0)

            temperature = base_temp + hour_adjustment
            precipitation = random.randint(0, 100) if random.random() < 0.2 else 0
            status = random.choice(WEATHER_STATUSES)

            hourly_data.write(f"{hour}\t{code}\t{temperature}\t{precipitation}\t{status}\n")

    hourly_data.seek(0)

    try:
        cursor.copy_from(
            hourly_data,
            'weather_hourly',
            columns=('hour_of_day', 'location_code', 'temperature', 'precipitation', 'status'),
            sep='\t'
        )
        conn.commit()
    except Exception as e:
        conn.rollback()
        print(f"Error inserting hourly weather: {e}")
        # Fall back to regular inserts
        print("Falling back to regular inserts...")

        batch_size = 1000
        hourly_values = []

        for city in tqdm(cities_data):
            code, _, _, _, _, climate_type = city
            climate = CLIMATE_TYPES[climate_type]

            # Generate a base temperature for the day
            base_temp = random.randint(
                climate["min_temp_range"][1],
                climate["max_temp_range"][0] + (climate["max_temp_range"][1] - climate["max_temp_range"][0]) // 2
            )

            # Generate weather for each hour
            for hour in range(24):
                # Temperature varies by hour (cooler at night, warmer during day)
                hour_adjustment = 0
                if 6 <= hour <= 18:  # Daytime
                    hour_adjustment = random.randint(0, 10)
                else:  # Nighttime
                    hour_adjustment = random.randint(-10, 0)

                temperature = base_temp + hour_adjustment
                precipitation = random.randint(0, 100) if random.random() < 0.2 else 0
                status = random.choice(WEATHER_STATUSES)

                hourly_values.append((hour, code, temperature, precipitation, status))

                # Execute in batches
                if len(hourly_values) >= batch_size:
                    args_str = ','.join(cursor.mogrify("(%s,%s,%s,%s,%s)", x).decode('utf-8') for x in hourly_values)
                    cursor.execute("INSERT INTO weather_hourly (hour_of_day, location_code, temperature, precipitation, status) VALUES " + args_str)
                    conn.commit()
                    hourly_values = []

        # Insert remaining values
        if hourly_values:
            args_str = ','.join(cursor.mogrify("(%s,%s,%s,%s,%s)", x).decode('utf-8') for x in hourly_values)
            cursor.execute("INSERT INTO weather_hourly (hour_of_day, location_code, temperature, precipitation, status) VALUES " + args_str)
            conn.commit()

    cursor.close()
    print(f"Inserted hourly weather data for {len(cities_data)} locations (24 hours each).")

def main():
    parser = argparse.ArgumentParser(description='Seed database with weather data.')
    parser.add_argument('--host', default='localhost', help='Database host')
    parser.add_argument('--port', type=int, default=5433, help='Database port')
    parser.add_argument('--user', default='koko', help='Database user')
    parser.add_argument('--password', default='password', help='Database password')
    parser.add_argument('--dbname', default='weatherdb', help='Database name')
    parser.add_argument('--months', type=int, default=12, help='Number of months of daily data to generate')
    parser.add_argument('--city-count', type=int, default=2000, help='Number of cities to generate')
    parser.add_argument('--clean', action='store_true', help='Drop existing tables before creation')

    args = parser.parse_args()

    try:
        conn = psycopg2.connect(
            host=args.host,
            port=args.port,
            user=args.user,
            password=args.password,
            dbname=args.dbname
        )

        # Set client encoding to UTF-8
        conn.set_client_encoding('UTF8')

        if args.clean:
            cursor = conn.cursor()
            print("Dropping existing tables...")
            cursor.execute("DROP TABLE IF EXISTS weather_hourly, weather_daily, realtime_weather, locations CASCADE")
            conn.commit()
            cursor.close()

        # Create schema
        create_schema(conn)

        # Load city data
        start_time = time.time()
        print(f"Loading data for {args.city_count} cities...")
        cities_data = load_cities_data()[:args.city_count]

        # Insert data
        insert_locations(conn, cities_data)
        insert_realtime_weather(conn, cities_data)
        insert_daily_weather(conn, cities_data, args.months)
        insert_hourly_weather(conn, cities_data)

        # Calculate statistics
        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM locations")
        location_count = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM weather_daily")
        daily_count = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM weather_hourly")
        hourly_count = cursor.fetchone()[0]

        cursor.close()

        end_time = time.time()
        duration = end_time - start_time

        print("\n----- Seeding Complete -----")
        print(f"Total duration: {duration:.2f} seconds")
        print(f"Locations: {location_count}")
        print(f"Daily weather records: {daily_count}")
        print(f"Hourly weather records: {hourly_count}")
        print(f"Total records: {location_count + daily_count + hourly_count + location_count}")

        conn.close()

    except Exception as e:
        print(f"Error: {e}")
        return 1

    return 0

if __name__ == "__main__":
    exit(main())
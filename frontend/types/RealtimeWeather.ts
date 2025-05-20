export interface RealtimeWeather {
    location?: string;
    temperature: number;
    humidity: number;
    precipitation: number;
    status: string;
    wind_speed: number;
    last_updated?: string;
  }
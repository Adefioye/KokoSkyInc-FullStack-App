export interface HourlyForecast {
    hour_of_day: number;
    temperature: number;
    precipitation: number;
    status: string;
  }
  
  export interface HourlyWeather {
    location?: string;
    hourly_forecast: HourlyForecast[];
  }
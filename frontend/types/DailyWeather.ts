export interface DailyForecast {
    day_of_month: number;
    month: number;
    min_temp: number;
    max_temp: number;
    precipitation: number;
    status: string;
  }
  
  export interface DailyWeather {
    location?: string;
    daily_forecast: DailyForecast[];
  }
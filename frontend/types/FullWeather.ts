// Import necessary types using @ symbol
import { RealtimeWeather } from './RealtimeWeather';
import { HourlyForecast } from './HourlyWeather';
import { DailyForecast } from './DailyWeather';

export interface FullWeather {
    location?: string;
    realtime: RealtimeWeather;
    hourly_forecast: HourlyForecast[];
    daily_forecast: DailyForecast[];
}
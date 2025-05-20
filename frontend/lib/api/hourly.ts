import { HourlyWeather } from '@/types/HourlyWeather';
import { fetchApi } from './client';

const BASE_URL = "/api/v1/hourly";

export const hourlyApi = {
  getHourlyByIP: (currentHour: number) => 
    fetchApi<HourlyWeather>(BASE_URL, {
      headers: {
        'X-Current-Hour': currentHour.toString(),
      }
    }),
  
  getHourlyByLocationCode: (code: string, currentHour: number) => 
    fetchApi<HourlyWeather>(`${BASE_URL}/${encodeURIComponent(code)}`, {
      headers: {
        'X-Current-Hour': currentHour.toString(),
      }
    }),
  
  updateHourlyForecast: (code: string, hourlyForecasts: HourlyWeather['hourly_forecast']) => 
    fetchApi<HourlyWeather>(`${BASE_URL}/${encodeURIComponent(code)}`, {
      method: 'PUT',
      body: JSON.stringify(hourlyForecasts),
    }),
};
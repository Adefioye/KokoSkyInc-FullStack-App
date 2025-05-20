import { DailyWeather } from '@/types/DailyWeather';
import { fetchApi } from './client';

const BASE_URL = "/api/v1/daily";

export const dailyApi = {
  getDailyByIP: () => 
    fetchApi<DailyWeather>(BASE_URL),
  
  getDailyByLocationCode: (code: string) => 
    fetchApi<DailyWeather>(`${BASE_URL}/${encodeURIComponent(code)}`),
  
  updateDailyForecast: (code: string, dailyForecasts: DailyWeather['daily_forecast']) => 
    fetchApi<DailyWeather>(`${BASE_URL}/${encodeURIComponent(code)}`, {
      method: 'PUT',
      body: JSON.stringify(dailyForecasts),
    }),
};
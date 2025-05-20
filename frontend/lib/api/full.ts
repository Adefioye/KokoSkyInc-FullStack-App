import { FullWeather } from '@/types/FullWeather';
import { fetchApi } from './client';

const BASE_URL = "/api/v1/full";

export const fullApi = {
  getFullWeatherByIP: () => 
    fetchApi<FullWeather>(BASE_URL),
  
  getFullWeatherByLocationCode: (code: string) => 
    fetchApi<FullWeather>(`${BASE_URL}/${encodeURIComponent(code)}`),
  
  updateFullWeather: (code: string, fullWeather: Omit<FullWeather, 'location'>) => 
    fetchApi<FullWeather>(`${BASE_URL}/${encodeURIComponent(code)}`, {
      method: 'PUT',
      body: JSON.stringify(fullWeather),
    }),
};
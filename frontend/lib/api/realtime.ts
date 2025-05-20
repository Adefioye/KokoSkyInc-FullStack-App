import { RealtimeWeather } from '@/types/RealtimeWeather';
import { fetchApi } from './client';

const BASE_URL = "/api/v1/realtime";

export const realtimeApi = {
  getRealtimeByIP: () => 
    fetchApi<RealtimeWeather>(BASE_URL),
  
  getRealtimeByLocationCode: (code: string) => 
    fetchApi<RealtimeWeather>(`${BASE_URL}/${encodeURIComponent(code)}`),
  
  updateRealtimeWeather: (code: string, weatherData: Omit<RealtimeWeather, 'location' | 'last_updated'>) => 
    fetchApi<RealtimeWeather>(`${BASE_URL}/${encodeURIComponent(code)}`, {
      method: 'PUT',
      body: JSON.stringify(weatherData),
    }),
};
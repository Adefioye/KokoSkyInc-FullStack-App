import { Location } from '@/types/Location';
import { fetchApi } from './client';

const BASE_URL = "/api/v1/locations";

export const locationsApi = {
  getAllLocations: () => fetchApi<Location[]>(BASE_URL),
  
  getLocation: (code: string) => 
    fetchApi<Location>(`${BASE_URL}/${encodeURIComponent(code)}`),
  
  addLocation: (location: Location) => 
    fetchApi<Location>(BASE_URL, {
      method: 'POST',
      body: JSON.stringify(location),
    }),
  
  updateLocation: (location: Location) => 
    fetchApi<Location>(BASE_URL, {
      method: 'PUT',
      body: JSON.stringify(location),
    }),
  
  deleteLocation: (code: string) => 
    fetchApi(`${BASE_URL}/${encodeURIComponent(code)}`, {
      method: 'DELETE',
    }),
};
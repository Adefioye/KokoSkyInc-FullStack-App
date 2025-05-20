import { useCallback, useEffect, useState } from 'react';
import { Location } from '@/types/Location';
import { locationsApi } from '@/lib/api/locations';

export function useLocations() {
  const [locations, setLocations] = useState<Location[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchLocations = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await locationsApi.getAllLocations();
      setLocations(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch locations');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const addLocation = useCallback(async (location: Location) => {
    setIsLoading(true);
    setError(null);
    try {
      const newLocation = await locationsApi.addLocation(location);
      setLocations(prev => [...prev, newLocation]);
      return newLocation;
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to add location');
      console.error(err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const updateLocation = useCallback(async (location: Location) => {
    setIsLoading(true);
    setError(null);
    try {
      const updatedLocation = await locationsApi.updateLocation(location);
      setLocations(prev => 
        prev.map(loc => loc.code === updatedLocation.code ? updatedLocation : loc)
      );
      return updatedLocation;
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update location');
      console.error(err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const deleteLocation = useCallback(async (code: string) => {
    setIsLoading(true);
    setError(null);
    try {
      await locationsApi.deleteLocation(code);
      setLocations(prev => prev.filter(loc => loc.code !== code));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete location');
      console.error(err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchLocations();
  }, [fetchLocations]);

  return {
    locations,
    isLoading,
    error,
    fetchLocations,
    addLocation,
    updateLocation,
    deleteLocation
  };
}

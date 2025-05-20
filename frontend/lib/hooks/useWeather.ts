import { useCallback, useEffect, useState } from 'react';
import { realtimeApi } from '@/lib/api/realtime';
import { hourlyApi } from '@/lib/api/hourly';
import { dailyApi } from '@/lib/api/daily';
import { fullApi } from '@/lib/api/full';
import { RealtimeWeather } from '@/types/RealtimeWeather';
import { HourlyWeather } from '@/types/HourlyWeather';
import { DailyWeather } from '@/types/DailyWeather';
import { FullWeather } from '@/types/FullWeather';

export function useRealtimeWeather(locationCode?: string) {
  const [weather, setWeather] = useState<RealtimeWeather | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchWeather = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      let data: RealtimeWeather;
      if (locationCode) {
        data = await realtimeApi.getRealtimeByLocationCode(locationCode);
      } else {
        data = await realtimeApi.getRealtimeByIP();
      }
      setWeather(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch realtime weather');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, [locationCode]);

  useEffect(() => {
    fetchWeather();
  }, [fetchWeather]);

  return {
    weather,
    isLoading,
    error,
    fetchWeather
  };
}

export function useHourlyWeather(locationCode?: string) {
  const [hourlyWeather, setHourlyWeather] = useState<HourlyWeather | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchHourlyWeather = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const currentHour = new Date().getHours();
      let data: HourlyWeather;
      
      if (locationCode) {
        data = await hourlyApi.getHourlyByLocationCode(locationCode, currentHour);
      } else {
        data = await hourlyApi.getHourlyByIP(currentHour);
      }
      
      setHourlyWeather(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch hourly weather');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, [locationCode]);

  useEffect(() => {
    fetchHourlyWeather();
  }, [fetchHourlyWeather]);

  return {
    hourlyWeather,
    isLoading,
    error,
    fetchHourlyWeather
  };
}

export function useDailyWeather(locationCode?: string) {
  const [dailyWeather, setDailyWeather] = useState<DailyWeather | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchDailyWeather = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      let data: DailyWeather;
      
      if (locationCode) {
        data = await dailyApi.getDailyByLocationCode(locationCode);
      } else {
        data = await dailyApi.getDailyByIP();
      }
      
      setDailyWeather(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch daily weather');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, [locationCode]);

  useEffect(() => {
    fetchDailyWeather();
  }, [fetchDailyWeather]);

  return {
    dailyWeather,
    isLoading,
    error,
    fetchDailyWeather
  };
}

export function useFullWeather(locationCode?: string) {
  const [fullWeather, setFullWeather] = useState<FullWeather | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchFullWeather = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      let data: FullWeather;
      
      if (locationCode) {
        data = await fullApi.getFullWeatherByLocationCode(locationCode);
      } else {
        data = await fullApi.getFullWeatherByIP();
      }
      
      setFullWeather(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch full weather data');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, [locationCode]);

  useEffect(() => {
    fetchFullWeather();
  }, [fetchFullWeather]);

  return {
    fullWeather,
    isLoading,
    error,
    fetchFullWeather
  };
}
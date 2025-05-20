package com.KokoSky.WeatherService;

import com.KokoSky.WeatherService.dailyWeather.DailyWeather;
import com.KokoSky.WeatherService.dailyWeather.DailyWeatherDTO;
import com.KokoSky.WeatherService.fullWeather.FullWeatherDTO;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeather;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeatherDTO;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeather;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeatherDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		var typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
		typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);

		var typeMap2 = mapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
		typeMap2.addMapping(HourlyWeatherDTO::getHourOfDay,
				(dest, value) ->	dest.getId().setHourOfDay(value != null ? (int) value : 0));

		var typeMap3 = mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
		typeMap3.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO::setDayOfMonth);
		typeMap3.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO::setMonth);

		var typeMap4 = mapper.typeMap(DailyWeatherDTO.class, DailyWeather.class);
		typeMap4.addMapping(src -> src.getDayOfMonth(),
				(dest, value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0));
		typeMap4.addMapping(src -> src.getMonth(),
				(dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0));

		var typeMap5 = mapper.typeMap(Location.class, FullWeatherDTO.class);
		typeMap5.addMapping(src -> src.toString(), FullWeatherDTO::setLocation);

		var typeMap6 = mapper.typeMap(RealtimeWeatherDTO.class, RealtimeWeather.class);
		typeMap6.addMappings(m -> m.skip(RealtimeWeather::setLocation));

		return mapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}

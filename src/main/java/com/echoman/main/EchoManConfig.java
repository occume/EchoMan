package com.echoman.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:beans.xml")
public class EchoManConfig {
	
//	@Bean(name="roomService")
//	public RoomService roomService(){
//		RoomService rs = new SimpleRoomService();
//		for(int i = 1; i < 11; i++){
//			rs.createRoom(i, "room_" + i);
//		}
//		return rs;
//	}

}

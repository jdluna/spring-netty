package com.example.netty.service;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.solab.iso8583.IsoMessage;

@Service
public class SimpleServiceImpl implements SimpleService {
	
	private static Logger logger = LoggerFactory.getLogger(SimpleServiceImpl.class);
	
	@Override
	public void processMessage(IsoMessage message) {
		try {
			File folder = new File("messages");
			if (!folder.exists()) {
				folder.mkdir();
			}
	
			File file = new File(String.format("messages/iso8583_%s_%d.txt", "", 2));
	
			FileWriter fw = new FileWriter(file, true);
	
			fw.write(message.debugString());
			fw.write("\n");
			fw.close();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}

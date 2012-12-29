package spring25web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RepeatTask implements Runnable {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
	public void run() {
		log.info("RepeatTask#run() ticked!");
	}
}

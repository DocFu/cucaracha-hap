package de.plasmawolke.cucaracha;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.HumiditySensor;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;
import de.plasmawolke.cucaracha.wsqlcplusdmx.BaseControl;

public class PassTelekomChecker extends BaseControl implements HumiditySensor {

	private final static Logger logger = LoggerFactory.getLogger(PassTelekomChecker.class);

	private static final int minOfDay = 60 * 24;

	private double remainingPercent = 0.0;

	public PassTelekomChecker() {
		this(new CucarachaAccessory());
	}

	public PassTelekomChecker(CucarachaAccessory cucarachaAccessory) {
		super(cucarachaAccessory);
		setHapId(404);
		setHapLabel("Internet (verbleibend");
		setHapManufacturer("Fozzy Garden Inc.");
		setHapModel("HRT-Converter");
		setHapSerialNo("2018.5");
	}

	@Override
	public CompletableFuture<Double> getCurrentRelativeHumidity() {
		return CompletableFuture.completedFuture(remainingPercent);
	}

	public void populate() throws Exception {

		String url = "http://pass.telekom.de/";

		logger.debug("Checking '" + url + "'...");

		// Instantiate HttpClient
		HttpClient httpClient = new HttpClient();

		// Configure HttpClient, for example:
		httpClient.setFollowRedirects(false);

		// Start HttpClient
		httpClient.start();
		ContentResponse response = httpClient.newRequest(url).method(HttpMethod.GET)
				.agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0").send();

		String html = response.getContentAsString();

		httpClient.stop();

		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Elements remainingTimeElement = doc.select("td.infoValue.remainingTime");

		if (remainingTimeElement == null || remainingTimeElement.isEmpty()) {
			logger.error("Could not find an element with class 'td.infoValue.remainingTime'");
			return;
		}

		// 10 Std. 38 Min.
		String remainingTimeElementText = remainingTimeElement.text();

		logger.info("Content of remaining time: " + remainingTimeElementText);

		int stunden = 0;
		int minuten = 0;
		float verbleibend = 0;

		String[] tokens = StringUtils.splitByCharacterTypeCamelCase(remainingTimeElementText);

		for (int i = 0; i < tokens.length; i++) {
			String value = tokens[i].trim();

			if (StringUtils.isNumeric(value)) {
				if (i == 0) {
					stunden = Integer.parseInt(value);
				} else if (i > 0) {
					minuten = Integer.parseInt(value);
					break;
				}
			}
		}

		logger.info("Stunden " + stunden);
		logger.info("Minuten " + minuten);

		verbleibend = (stunden * 60) + minuten;
		float hundert = 100;

		logger.info(verbleibend + " minuten");

		float result = (hundert / minOfDay) * verbleibend;

		int intResult = (int) Math.round(result);

		logger.info(result + "%");
		logger.info(intResult + "%");

		remainingPercent = result;

		if (getPowerStateChangeCallback() != null) {
			getPowerStateChangeCallback().changed();
		} else {
			logger.warn("powerstate callback was null " + this);
		}

		return;

	}

	public static void main(String[] args) throws Exception {

	}

	@Override
	public void subscribeCurrentRelativeHumidity(HomekitCharacteristicChangeCallback callback) {
		setPowerStateChangeCallback(callback);

	}

	@Override
	public void unsubscribeCurrentRelativeHumidity() {
		setPowerStateChangeCallback(null);

	}

}

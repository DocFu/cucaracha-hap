package de.plasmawolke.cucaracha.wsqlcplusdmx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.cucaracha.model.CucarachaConfig;

public abstract class ButtonCollector {

	private final static Logger logger = LoggerFactory.getLogger(ButtonCollector.class);

	public static List<VirtualConsoleButton> populate(String url) throws Exception {

		List<VirtualConsoleButton> buttons = new ArrayList<>();

		logger.debug("Populating buttons from " + url);

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
		Elements links = doc.select("a.vcbutton");

		logger.debug("Found " + links.size() + " buttons.");

		for (Element element : links) {

			String id = element.attr("id");
			String name = element.text().trim();
			boolean enabled = element.attr("style").contains("#00E600");

			if (name.startsWith("*")) {
				name = name.replace("*", "").trim();

				VirtualConsoleButton vcb = new VirtualConsoleButton();
				vcb.setEnabled(enabled);
				vcb.setId(Integer.parseInt(id));
				vcb.setName(name);

				buttons.add(vcb);

				logger.debug("Collected " + name + " (" + id + ") with state " + enabled);
			}

		}
		return buttons;

	}

	public static void main(String[] args) throws Exception {

		ButtonCollector.populate(new CucarachaConfig().buildQlcPlusVirtualConsoleUrl());

	}

}

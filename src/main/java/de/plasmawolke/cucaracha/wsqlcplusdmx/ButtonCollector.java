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

public class ButtonCollector {

	private final static Logger logger = LoggerFactory.getLogger(ButtonCollector.class);

	private String url = "http://schneestreamchen.local:9999/";

	private List<VirtualConsoleButton> buttons = new ArrayList<>();

	public void populate() throws Exception {

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

				System.out.println(id + " " + enabled + " --> " + name);
			}

		}

	}

	public List<VirtualConsoleButton> getButtons() {
		return buttons;
	}

	public static void main(String[] args) throws Exception {
		ButtonCollector bc = new ButtonCollector();
		bc.populate();
		System.out.println(bc.getButtons().size());
	}

}

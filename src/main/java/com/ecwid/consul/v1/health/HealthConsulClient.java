package com.ecwid.consul.v1.health;

import com.ecwid.consul.SingleUrlParameters;
import com.ecwid.consul.UrlParameters;
import com.ecwid.consul.json.GsonFactory;
import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.transport.RawResponse;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.Check;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @author Vasily Vasilkov (vgv@ecwid.com)
 */
public final class HealthConsulClient implements HealthClient {

	private final ConsulRawClient rawClient;

	public HealthConsulClient(ConsulRawClient rawClient) {
		this.rawClient = rawClient;
	}

	public HealthConsulClient() {
		this(new ConsulRawClient());
	}

	public HealthConsulClient(String agentHost) {
		this(new ConsulRawClient(agentHost));
	}

	public HealthConsulClient(String agentHost, int agentPort) {
		this(new ConsulRawClient(agentHost, agentPort));
	}

	@Override
	public Response<List<Check>> getHealthChecksForNode(String nodeName, QueryParams queryParams) {
		RawResponse rawResponse = rawClient.makeGetRequest("/v1/health/node/" + nodeName, queryParams);

		if (rawResponse.getStatusCode() == 200) {
			List<Check> value = GsonFactory.getGson().fromJson(rawResponse.getContent(), new TypeToken<List<Check>>() {
			}.getType());
			return new Response<List<Check>>(value, rawResponse);
		} else {
			throw new OperationException(rawResponse);
		}
	}

	@Override
	public Response<List<Check>> getHealthChecksForService(String serviceName, QueryParams queryParams) {
		RawResponse rawResponse = rawClient.makeGetRequest("/v1/health/checks/" + serviceName, queryParams);

		if (rawResponse.getStatusCode() == 200) {
			List<Check> value = GsonFactory.getGson().fromJson(rawResponse.getContent(), new TypeToken<List<Check>>() {
			}.getType());
			return new Response<List<Check>>(value, rawResponse);
		} else {
			throw new OperationException(rawResponse);
		}
	}

	@Override
	public Response<List<com.ecwid.consul.v1.health.model.HealthService>> getHealthServices(String serviceName, boolean onlyPassing, QueryParams queryParams) {
		return getHealthServices(serviceName, null, onlyPassing, queryParams);
	}

	@Override
	public Response<List<com.ecwid.consul.v1.health.model.HealthService>> getHealthServices(String serviceName, String tag, boolean onlyPassing, QueryParams queryParams) {
		UrlParameters tagParams = tag != null ? new SingleUrlParameters("tag", tag) : null;
		UrlParameters passingParams = onlyPassing ? new SingleUrlParameters("passing") : null;

		RawResponse rawResponse = rawClient.makeGetRequest("/v1/health/service/" + serviceName, tagParams, passingParams, queryParams);

		if (rawResponse.getStatusCode() == 200) {
			List<com.ecwid.consul.v1.health.model.HealthService> value = GsonFactory.getGson().fromJson(rawResponse.getContent(), new TypeToken<List<com.ecwid.consul.v1.health.model.HealthService>>() {
			}.getType());
			return new Response<List<com.ecwid.consul.v1.health.model.HealthService>>(value, rawResponse);
		} else {
			throw new OperationException(rawResponse);
		}
	}

	@Override
	public Response<List<Check>> getHealthChecksState(QueryParams queryParams) {
		return getHealthChecksState(null, queryParams);
	}

	@Override
	public Response<List<Check>> getHealthChecksState(Check.CheckStatus checkStatus, QueryParams queryParams) {
		String status = checkStatus == null ? "ANY" : checkStatus.name().toLowerCase();
		RawResponse rawResponse = rawClient.makeGetRequest("/v1/health/state/" + status, queryParams);

		if (rawResponse.getStatusCode() == 200) {
			List<Check> value = GsonFactory.getGson().fromJson(rawResponse.getContent(), new TypeToken<List<Check>>() {
			}.getType());
			return new Response<List<Check>>(value, rawResponse);
		} else {
			throw new OperationException(rawResponse);
		}
	}

}

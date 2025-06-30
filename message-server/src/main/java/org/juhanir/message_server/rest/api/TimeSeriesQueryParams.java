package org.juhanir.message_server.rest.api;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

public class TimeSeriesQueryParams {

    @QueryParam("from")
    @Parameter(description = "Start timestamp in ISO 8601 format")
    public String from;

    @QueryParam("to")
    @Parameter(description = "End timestamp in ISO 8601 format")
    public String to;

    @QueryParam("limit")
    @DefaultValue("100")
    @Parameter(description = "Maximum number of results")
    public String limit;

    @QueryParam("sort")
    @DefaultValue("asc")
    @Parameter(description = "Sort order for time: asc|desc")
    public String sort;

}

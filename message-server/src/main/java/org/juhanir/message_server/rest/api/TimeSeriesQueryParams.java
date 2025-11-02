package org.juhanir.message_server.rest.api;

import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.List;

public class TimeSeriesQueryParams {

    @QueryParam("from")
    @Parameter(description = "Start timestamp in ISO 8601 format")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3,6})?Z$",
            message = "from timestamp not accepted format"
    )
    public String from;

    @QueryParam("to")
    @Parameter(description = "End timestamp in ISO 8601 format")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3,6})?Z$",
            message = "to timestamp not accepted format"
    )
    public String to;

    @QueryParam("offset")
    @DefaultValue("0")
    @Parameter(description = "Pagination offset")
    public int offset;

    @QueryParam("limit")
    @DefaultValue("100")
    @Parameter(description = "Maximum number of results")
    public int limit;

    @QueryParam("sort")
    @DefaultValue("desc")
    @Parameter(description = "Sort order for time: asc|desc")
    public String sort;

    @QueryParam("device")
    @Parameter(description = "Device id. These can be chained.")
    public List<String> deviceIdentifiers;

    @QueryParam("signal")
    @Parameter(description = "Signal name. These can be chained.")
    public List<String> signals;

}

package edu.emory.pathology.hladpb1.webservices;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JerseyMapperProvider implements ContextResolver<ObjectMapper> {
    private static ObjectMapper om = null;
    @Override
    public ObjectMapper getContext(Class<?> type) {
        if(om == null) {
            om = new ObjectMapper();
            om.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        }
        return om;
    }
}

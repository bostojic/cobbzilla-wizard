package org.cobbzilla.wizard.resources;

import lombok.extern.slf4j.Slf4j;
import org.cobbzilla.util.json.JsonUtil;
import org.cobbzilla.util.string.StringUtil;
import org.cobbzilla.wizard.dao.DAO;
import org.cobbzilla.wizard.model.Identifiable;
import org.cobbzilla.wizard.model.ResultPage;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractResource<T extends Identifiable> {

    public static final String UUID_PARAM = "uuid";
    public static final String UUID = "{"+UUID_PARAM+"}";

//    protected abstract AbstractCRUDDAO<T> dao ();
    protected abstract DAO<T> dao ();

    protected abstract String getEndpoint();

    @GET
    public Response index(@QueryParam(ResultPage.PARAM_USE_PAGINATION) Boolean usePagination,
                          @QueryParam(ResultPage.PARAM_PAGE_NUMBER) Integer pageNumber,
                          @QueryParam(ResultPage.PARAM_PAGE_SIZE) Integer pageSize,
                          @QueryParam(ResultPage.PARAM_SORT_FIELD) String sortField,
                          @QueryParam(ResultPage.PARAM_SORT_ORDER) String sortOrder,
                          @QueryParam(ResultPage.PARAM_FILTER) String filter,
                          @QueryParam(ResultPage.PARAM_BOUNDS) String bounds) {

        if (usePagination == null || !usePagination) return findAll();

        final DAO<T> dao = dao();
        final Map<String, String> boundsMap = parseBounds(bounds, dao);
        return Response.ok(dao.query(new ResultPage(pageNumber, pageSize, sortField, sortOrder, filter, boundsMap))).build();
    }

    public static Map<String, String> parseBounds(String bounds, DAO dao) {
        Map<String, String> boundsMap = null;
        if (!StringUtil.empty(bounds)) {
            Class<? extends Map<String, String>> clazz = dao.boundsClass();
            try {
                boundsMap = JsonUtil.fromJson(bounds, clazz);
            } catch (Exception e) {
                log.warn("index: invalid bounds (" + bounds + ") for boundsClass " + clazz + ": " + e);
            }
        }
        return boundsMap;
    }

    protected Response findAll() {
        return Response.ok(dao().findAll()).build();
    }

    @POST
    public Response create(@Valid T thing) {

        final Object context;
        context = preCreate(thing);
        thing = dao().create(thing);
        thing = postCreate(thing, context);

        final URI location = URI.create(thing.getUuid());
        return Response.created(location).build();
    }

    protected Object preCreate(T thing) { return null; }
    protected T postCreate(T thing, Object context) { return thing; }

    @Path("/"+UUID)
    @GET
    public Response find(@PathParam(UUID_PARAM) String uuid) {
        final T thing = dao().findByUuid(uuid);
        return thing == null ? ResourceUtil.notFound(uuid) : Response.ok(postProcess(thing)).build();
    }

    protected T postProcess(T thing) { return thing; }

    @Path("/"+UUID)
    @PUT
    public Response update(@PathParam(UUID_PARAM) String uuid, @Valid T thing) {
        Response response;
        final DAO<T> dao = dao();
        final T found = dao.findByUuid(uuid);
        if (found != null) {
            thing.setUuid(uuid);
            final Object context = preUpdate(thing);
            dao.update(thing);
            thing = postUpdate(thing, context);
            response = Response.noContent().build();
        } else {
            response = ResourceUtil.notFound(uuid);
        }
        return response;
    }

    protected Object preUpdate(T thing) { return null; }
    protected T postUpdate(T thing, Object context) { return thing; }

    @Path("/"+UUID)
    @DELETE
    public Response delete(@PathParam(UUID_PARAM) String uuid) {
        final DAO<T> dao = dao();
        final T found = dao.findByUuid(uuid);
        if (found == null) return ResourceUtil.notFound();
        final Object context = preDelete(found);
        dao.delete(uuid);
        postDelete(found, context);
        return Response.noContent().build();
    }

    protected Object preDelete(T thing) { return null; }
    protected void postDelete(T thing, Object context) {}

}

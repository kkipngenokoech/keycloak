/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.services.resources.account;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.NoCache;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.representations.account.ClientRepresentation;
import org.keycloak.representations.account.SessionRepresentation;
import org.keycloak.services.managers.Auth;
import org.keycloak.utils.UserAgentParser;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class SessionResource {

    private final KeycloakSession session;
    private final Auth auth;
    private final EventBuilder event;

    public SessionResource(KeycloakSession session, Auth auth, EventBuilder event) {
        this.session = session;
        this.auth = auth;
        this.event = event;
    }

    /**
     * Get session information.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public List<SessionRepresentation> toRepresentation() {
        RealmModel realm = auth.getRealm();
        List<UserSessionModel> sessions = session.sessions().getUserSessionsStream(realm, auth.getUser()).collect(Collectors.toList());
        List<SessionRepresentation> reps = new LinkedList<>();
        for (UserSessionModel s : sessions) {
            SessionRepresentation rep = new SessionRepresentation();
            rep.setId(s.getId());
            rep.setIpAddress(s.getIpAddress());
            rep.setStarted(s.getStarted() * 1000L);
            rep.setLastAccess(s.getLastSessionRefresh() * 1000L);
            rep.setExpires(s.getStarted() * 1000L + realm.getSsoSessionMaxLifespan() * 1000L);
            
            // Parse user agent for device information
            String userAgent = s.getNote("user_agent");
            if (userAgent != null) {
                UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
                rep.setBrowser(deviceInfo.getBrowser());
                rep.setBrowserVersion(deviceInfo.getBrowserVersion());
                rep.setOs(deviceInfo.getOperatingSystem());
                rep.setOsVersion(deviceInfo.getOsVersion());
                rep.setDevice(deviceInfo.getDeviceType());
            }
            
            rep.setCurrent(s.getId().equals(auth.getSession().getId()));
            
            List<ClientRepresentation> clients = new LinkedList<>();
            for (AuthenticatedClientSessionModel clientSession : s.getAuthenticatedClientSessions().values()) {
                ClientModel client = clientSession.getClient();
                ClientRepresentation clientRep = new ClientRepresentation();
                clientRep.setClientId(client.getClientId());
                clientRep.setClientName(client.getName());
                clients.add(clientRep);
            }
            rep.setClients(clients);
            
            reps.add(rep);
        }
        return reps;
    }

    /**
     * Remove sessions
     *
     * @param sessionId
     * @return
     */
    @Path("/{id}")
    @DELETE
    public Response logout(@PathParam("id") String sessionId) {
        UserSessionModel userSession = session.sessions().getUserSession(auth.getRealm(), sessionId);
        if (userSession != null && userSession.getUser().equals(auth.getUser())) {
            session.sessions().removeUserSession(auth.getRealm(), userSession);
            event.event(EventType.LOGOUT).detail(Details.USERNAME, auth.getUser().getUsername()).success();
        }
        return Response.noContent().build();
    }

    /**
     * Remove all sessions
     *
     * @return
     */
    @DELETE
    public Response logoutAll() {
        session.sessions().getUserSessionsStream(auth.getRealm(), auth.getUser()).collect(Collectors.toList())
                .forEach(userSession -> session.sessions().removeUserSession(auth.getRealm(), userSession));
        
        event.event(EventType.LOGOUT).detail(Details.USERNAME, auth.getUser().getUsername()).success();
        return Response.noContent().build();
    }
}
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

package org.keycloak.services.resources.admin.permissions;

import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.model.Scope;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.authorization.AbstractPolicyRepresentation;
import org.keycloak.services.resources.admin.AdminAuth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Fine-grained permissions V2 for client management.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientPermissionsV2 {
    public static final String VIEW_SCOPE = "view";
    public static final String MANAGE_SCOPE = "manage";
    public static final String CONFIGURE_SCOPE = "configure";
    public static final String MAP_ROLES_SCOPE = "map-roles";
    public static final String MAP_ROLES_CLIENT_SCOPE_SCOPE = "map-roles-client-scope";
    public static final String MAP_ROLES_COMPOSITE_SCOPE = "map-roles-composite";
    public static final String TOKEN_EXCHANGE_SCOPE = "token-exchange";

    protected final KeycloakSession session;
    protected final RealmModel realm;
    protected final AuthorizationProvider authz;
    protected final MgmtPermissions root;
    protected final AdminAuth auth;

    public ClientPermissionsV2(KeycloakSession session, RealmModel realm, AuthorizationProvider authz, MgmtPermissions root, AdminAuth auth) {
        this.session = session;
        this.realm = realm;
        this.authz = authz;
        this.root = root;
        this.auth = auth;
    }

    public boolean canList() {
        return canView();
    }

    public boolean canView() {
        if (root.hasOneAdminRole(MgmtPermissions.VIEW_CLIENTS, MgmtPermissions.MANAGE_CLIENTS)) return true;
        
        ResourceServer server = root.realmResourceServer();
        if (server == null) return false;

        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, MgmtPermissions.CLIENTS_RESOURCE);
        if (resource == null) return false;

        Policy policy = authz.getStoreFactory().getPolicyStore().findByName(server, MgmtPermissions.CLIENTS_POLICY);
        if (policy == null) return false;

        Set<String> scopes = new HashSet<>();
        scopes.add(VIEW_SCOPE);
        scopes.add(MANAGE_SCOPE); // manage scope implies view
        return root.hasResourceScopePermission(resource, scopes, policy);
    }

    public boolean canView(ClientModel client) {
        if (root.hasOneAdminRole(MgmtPermissions.VIEW_CLIENTS, MgmtPermissions.MANAGE_CLIENTS)) return true;
        
        ResourceServer server = root.realmResourceServer();
        if (server == null) return false;

        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, getClientResourceName(client));
        if (resource == null) return false;

        Policy policy = authz.getStoreFactory().getPolicyStore().findByName(server, getClientPolicyName(client));
        if (policy == null) return false;

        Set<String> scopes = new HashSet<>();
        scopes.add(VIEW_SCOPE);
        scopes.add(MANAGE_SCOPE); // manage scope implies view
        return root.hasResourceScopePermission(resource, scopes, policy);
    }

    public boolean canManage() {
        if (root.hasOneAdminRole(MgmtPermissions.MANAGE_CLIENTS)) return true;
        
        ResourceServer server = root.realmResourceServer();
        if (server == null) return false;

        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, MgmtPermissions.CLIENTS_RESOURCE);
        if (resource == null) return false;

        Policy policy = authz.getStoreFactory().getPolicyStore().findByName(server, MgmtPermissions.CLIENTS_POLICY);
        if (policy == null) return false;

        Set<String> scopes = new HashSet<>();
        scopes.add(MANAGE_SCOPE);
        return root.hasResourceScopePermission(resource, scopes, policy);
    }

    public boolean canManage(ClientModel client) {
        if (root.hasOneAdminRole(MgmtPermissions.MANAGE_CLIENTS)) return true;
        
        ResourceServer server = root.realmResourceServer();
        if (server == null) return false;

        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, getClientResourceName(client));
        if (resource == null) return false;

        Policy policy = authz.getStoreFactory().getPolicyStore().findByName(server, getClientPolicyName(client));
        if (policy == null) return false;

        Set<String> scopes = new HashSet<>();
        scopes.add(MANAGE_SCOPE);
        return root.hasResourceScopePermission(resource, scopes, policy);
    }

    public boolean canConfigure() {
        return canManage();
    }

    public boolean canConfigure(ClientModel client) {
        return canManage(client);
    }

    public void requireList() {
        if (!canList()) {
            throw new ForbiddenException();
        }
    }

    public void requireView() {
        if (!canView()) {
            throw new ForbiddenException();
        }
    }

    public void requireView(ClientModel client) {
        if (!canView(client)) {
            throw new ForbiddenException();
        }
    }

    public void requireManage() {
        if (!canManage()) {
            throw new ForbiddenException();
        }
    }

    public void requireManage(ClientModel client) {
        if (!canManage(client)) {
            throw new ForbiddenException();
        }
    }

    public void requireConfigure() {
        requireManage();
    }

    public void requireConfigure(ClientModel client) {
        requireManage(client);
    }

    private String getClientResourceName(ClientModel client) {
        return "client.resource." + client.getId();
    }

    private String getClientPolicyName(ClientModel client) {
        return "client.policy." + client.getId();
    }

    public Map<String, String[]> getPermissions(ClientModel client) {
        initializeClientPermissions(client);
        return root.getPermissions(getClientResourceName(client));
    }

    public boolean isPermissionsEnabled(ClientModel client) {
        ResourceServer server = root.realmResourceServer();
        if (server == null) return false;

        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, getClientResourceName(client));
        return resource != null;
    }

    public void setPermissionsEnabled(ClientModel client, boolean enabled) {
        if (enabled) {
            initializeClientPermissions(client);
        } else {
            deleteClientPermissions(client);
        }
    }

    public Resource initializeClientPermissions(ClientModel client) {
        ResourceServer server = root.realmResourceServer();
        if (server == null) return null;

        Scope viewScope = root.initializeScope(VIEW_SCOPE, server);
        Scope manageScope = root.initializeScope(MANAGE_SCOPE, server);
        Scope configureScope = root.initializeScope(CONFIGURE_SCOPE, server);
        Scope mapRolesScope = root.initializeScope(MAP_ROLES_SCOPE, server);
        Scope mapRolesClientScopeScope = root.initializeScope(MAP_ROLES_CLIENT_SCOPE_SCOPE, server);
        Scope mapRolesCompositeScope = root.initializeScope(MAP_ROLES_COMPOSITE_SCOPE, server);
        Scope tokenExchangeScope = root.initializeScope(TOKEN_EXCHANGE_SCOPE, server);

        String resourceName = getClientResourceName(client);
        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, resourceName);
        if (resource == null) {
            resource = authz.getStoreFactory().getResourceStore().create(server, resourceName, server.getClientId());
            Set<Scope> scopesSet = new HashSet<>();
            scopesSet.add(viewScope);
            scopesSet.add(manageScope);
            scopesSet.add(configureScope);
            scopesSet.add(mapRolesScope);
            scopesSet.add(mapRolesClientScopeScope);
            scopesSet.add(mapRolesCompositeScope);
            scopesSet.add(tokenExchangeScope);
            resource.updateScopes(scopesSet);
        }
        return resource;
    }

    public void deleteClientPermissions(ClientModel client) {
        ResourceServer server = root.realmResourceServer();
        if (server == null) return;

        Resource resource = authz.getStoreFactory().getResourceStore().findByName(server, getClientResourceName(client));
        if (resource != null) {
            authz.getStoreFactory().getResourceStore().delete(resource.getId());
        }

        Policy policy = authz.getStoreFactory().getPolicyStore().findByName(server, getClientPolicyName(client));
        if (policy != null) {
            authz.getStoreFactory().getPolicyStore().delete(policy.getId());
        }
    }

    public Policy clientPolicy(ClientModel client) {
        initializeClientPermissions(client);
        ResourceServer server = root.realmResourceServer();
        String policyName = getClientPolicyName(client);
        Policy policy = authz.getStoreFactory().getPolicyStore().findByName(server, policyName);
        if (policy == null) {
            policy = authz.getStoreFactory().getPolicyStore().create(server, policyName, "client", server.getClientId());
        }
        return policy;
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException() {
            super();
        }

        public ForbiddenException(String message) {
            super(message);
        }
    }
}
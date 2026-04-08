import { createBrowserRouter, Navigate } from "react-router-dom";
import { lazy, Suspense } from "react";
import { Spinner } from "@patternfly/react-core";
import { RouteErrorBoundary } from "./components/error/RouteErrorBoundary";

const LazyPersonalInfo = lazy(() => import("./pages/PersonalInfo"));
const LazyAccountSecurity = lazy(() => import("./pages/AccountSecurity"));
const LazyApplications = lazy(() => import("./pages/Applications"));
const LazyGroups = lazy(() => import("./pages/Groups"));
const LazyResources = lazy(() => import("./pages/Resources"));
const LazyOrganizations = lazy(() => import("./pages/Organizations"));

const SuspenseWrapper = ({ children }: { children: React.ReactNode }) => (
  <Suspense fallback={<Spinner />}>{children}</Suspense>
);

export const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyPersonalInfo />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "/personal-info",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyPersonalInfo />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "/account-security/*",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyAccountSecurity />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "/applications",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyApplications />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "/groups",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyGroups />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "/resources",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyResources />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "/organizations",
    element: (
      <RouteErrorBoundary>
        <SuspenseWrapper>
          <LazyOrganizations />
        </SuspenseWrapper>
      </RouteErrorBoundary>
    ),
    errorElement: <Navigate to="/" replace />,
  },
  {
    path: "*",
    element: <Navigate to="/" replace />,
  },
]);
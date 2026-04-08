import { RouterProvider } from "react-router-dom";
import { Page } from "@patternfly/react-core";
import { router } from "./router";
import { RouteErrorBoundary } from "./components/error/RouteErrorBoundary";

export default function App() {
  return (
    <Page>
      <RouteErrorBoundary>
        <RouterProvider router={router} />
      </RouteErrorBoundary>
    </Page>
  );
}
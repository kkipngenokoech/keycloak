import { Component, ErrorInfo, ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core";

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
  showAlert: boolean;
}

export class RouteErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, showAlert: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return {
      hasError: true,
      error,
      showAlert: true,
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Route error caught by boundary:", error, errorInfo);
  }

  handleAlertClose = () => {
    this.setState({ showAlert: false });
  };

  render() {
    if (this.state.hasError) {
      return (
        <>
          {this.state.showAlert && (
            <Alert
              variant="warning"
              title="Page not found"
              actionClose={
                <AlertActionCloseButton onClose={this.handleAlertClose} />
              }
            >
              The requested page could not be found. You have been redirected to the home page.
            </Alert>
          )}
          <Navigate to="/" replace />
        </>
      );
    }

    return this.props.children;
  }
}
import { PageSection, Text, TextContent, Title, Alert } from "@patternfly/react-core";
import { PropsWithChildren, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

type PageProps = {
  title: string;
  description: string;
};

export const Page = ({
  title,
  description,
  children,
}: PropsWithChildren<PageProps>) => {
  const location = useLocation();
  const navigate = useNavigate();
  const [showNotFoundAlert, setShowNotFoundAlert] = useState(false);

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    if (searchParams.get('redirected') === 'true') {
      setShowNotFoundAlert(true);
      const newUrl = location.pathname;
      navigate(newUrl, { replace: true });
    }
  }, [location, navigate]);

  const handleAlertClose = () => {
    setShowNotFoundAlert(false);
  };

  return (
    <>
      {showNotFoundAlert && (
        <Alert
          variant="warning"
          title="Page not found"
          isInline
          onClose={handleAlertClose}
          closeBtnAriaLabel="Close alert"
        >
          The requested page could not be found. You have been redirected to the home page.
        </Alert>
      )}
      <PageSection variant="light">
        <TextContent>
          <Title headingLevel="h1" data-testid="page-heading">
            {title}
          </Title>
          <Text component="p">{description}</Text>
        </TextContent>
      </PageSection>
      <PageSection variant="light">{children}</PageSection>
    </>
  );
};
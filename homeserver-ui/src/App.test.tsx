import { screen } from "@testing-library/react";
import { App } from "./App";
import { renderWithProviders } from "./utils/test-utils";

test("App landingpage", () => {
    renderWithProviders(<App />);

    const loggedInLabel = screen.getByText<HTMLLabelElement>("Home Management");

    expect(loggedInLabel).toBeVisible();
});

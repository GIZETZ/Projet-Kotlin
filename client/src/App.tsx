
import { Switch, Route } from "wouter";
import { queryClient } from "./lib/queryClient";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import Dashboard from "@/components/Dashboard";
import Login from "@/pages/Login";
import Register from "@/pages/Register";
import OperationDetails from "@/pages/OperationDetails";
import CotisationExceptionnelleDetails from "@/pages/CotisationExceptionnelleDetails";
import FondsCaisseDetails from "@/pages/FondsCaisseDetails";
import EditOperation from "@/pages/EditOperation";
import NewOperation from "@/pages/NewOperation";
import Profile from "@/pages/Profile";
import AdhesionSettings from "@/pages/AdhesionSettings";
import NotFound from "@/pages/not-found";
import { useState } from "react";

function Router() {
  return (
    <Switch>
      <Route path="/" component={Dashboard} />
      <Route path="/profile" component={Profile} />
      <Route path="/operation/new" component={NewOperation} />
      <Route path="/operation/:id" component={OperationDetails} />
      <Route path="/cotisation-exceptionnelle/:id" component={CotisationExceptionnelleDetails} />
      <Route path="/fonds-caisse/:id" component={FondsCaisseDetails} />
      <Route path="/operation/:id/edit" component={EditOperation} />
      <Route path="/adhesion-settings" component={AdhesionSettings} />
      <Route component={NotFound} />
    </Switch>
  );
}

function App() {
  const [currentUser, setCurrentUser] = useState<any>(() => {
    const saved = localStorage.getItem("currentUser");
    return saved ? JSON.parse(saved) : null;
  });

  const handleLoginSuccess = (user: any) => {
    localStorage.setItem("currentUser", JSON.stringify(user));
    setCurrentUser(user);
  };

  if (!currentUser) {
    return (
      <QueryClientProvider client={queryClient}>
        <TooltipProvider>
          <Toaster />
          <Switch>
            <Route path="/register" component={Register} />
            <Route path="*">
              <Login onLoginSuccess={handleLoginSuccess} />
            </Route>
          </Switch>
        </TooltipProvider>
      </QueryClientProvider>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <TooltipProvider>
        <Toaster />
        <Router />
      </TooltipProvider>
    </QueryClientProvider>
  );
}

export default App;

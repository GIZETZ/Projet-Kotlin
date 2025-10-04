import { Switch, Route } from "wouter";
import { queryClient } from "./lib/queryClient";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import Dashboard from "@/components/Dashboard";
import PINEntry from "@/components/PINEntry";
import { useState } from "react";

function Router() {
  return (
    <Switch>
      <Route path="/" component={Dashboard} />
      <Route component={Dashboard} />
    </Switch>
  );
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handlePINSubmit = (pin: string) => {
    console.log("PIN entered:", pin);
    // todo: remove mock functionality - validate PIN against stored hash
    if (pin === "1234") {
      setIsAuthenticated(true);
    } else {
      alert("Code PIN incorrect. Essayez 1234 pour la démo.");
    }
  };

  if (!isAuthenticated) {
    return <PINEntry onSubmit={handlePINSubmit} title="Entrez votre code PIN" />;
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

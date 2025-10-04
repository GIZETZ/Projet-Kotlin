import { useState, useEffect } from "react";
import { Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import OperationCard, { Operation } from "./OperationCard";
import QuickPaymentForm from "./QuickPaymentForm";
import ExportModal from "./ExportModal";
import BottomNav from "./BottomNav";
import { Plus, Search, Menu, Bell, User } from "lucide-react";

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState("dashboard");
  const [paymentFormOpen, setPaymentFormOpen] = useState(false);
  const [exportModalOpen, setExportModalOpen] = useState(false);
  const [selectedOperation, setSelectedOperation] = useState<Operation | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [operations, setOperations] = useState<any[]>([]);

  useEffect(() => {
    fetchOperations();
  }, []);

  const fetchOperations = async () => {
    try {
      const response = await fetch("/api/operations");
      if (response.ok) {
        const data = await response.json();
        setOperations(data.map((op: any) => ({
          ...op,
          dateDebut: new Date(op.dateDebut),
          dateFin: op.dateFin ? new Date(op.dateFin) : null,
          etat: op.statut === "En cours" ? "EN_COURS" : "TERMINE",
          nombrePayeurs: op.nombrePaiements || 0,
        })));
      }
    } catch (error) {
      console.error("Error fetching operations:", error);
    }
  };

  const filteredOperations = operations.filter((op) =>
    op.nom.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleExport = (operation: Operation) => {
    setSelectedOperation(operation);
    setExportModalOpen(true);
  };

  const handleTabChange = (tab: string) => {
    if (tab === "add") {
      setPaymentFormOpen(true);
    } else {
      setActiveTab(tab);
    }
  };

  return (
    <div className="min-h-screen bg-background pb-20 md:pb-0">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-card border-b border-border">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center gap-3">
            <Button size="icon" variant="ghost" className="md:hidden" data-testid="button-menu">
              <Menu className="w-5 h-5" />
            </Button>
            <h1 className="text-2xl font-bold">Tableau de bord</h1>
            <div className="flex-1" />
            <Link href="/adhesion-settings">
              <Button variant="outline" size="sm" className="hidden md:flex" data-testid="button-adhesion-settings">
                Paramètres d'adhésion
              </Button>
            </Link>
            <Button size="icon" variant="ghost" data-testid="button-notifications">
              <Bell className="w-5 h-5" />
            </Button>
            <Button size="icon" variant="ghost" data-testid="button-profile">
              <User className="w-5 h-5" />
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6 max-w-7xl">
        <div className="space-y-6">
          {/* Search and Actions */}
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                type="search"
                placeholder="Rechercher une opération..."
                className="pl-10"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                data-testid="input-search"
              />
            </div>
            <Link href="/operation/new">
              <Button className="hidden md:inline-flex whitespace-nowrap" data-testid="button-new-operation">
                <Plus className="w-4 h-4 mr-2" />
                Nouvelle opération
              </Button>
            </Link>
          </div>

          {/* Operations Grid */}
          {filteredOperations.length === 0 ? (
            <div className="text-center py-16 space-y-3">
              <div className="mx-auto w-16 h-16 rounded-full bg-muted flex items-center justify-center">
                <Search className="w-8 h-8 text-muted-foreground" />
              </div>
              <p className="text-lg font-medium text-foreground">Aucune opération trouvée</p>
              <p className="text-sm text-muted-foreground">Essayez de modifier votre recherche</p>
            </div>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {filteredOperations.map((operation) => {
                const getOperationPath = (op: Operation) => {
                  switch(op.type) {
                    case "ADHESION":
                      return `/operation/${op.id}`;
                    case "COTISATION_EXCEPTIONNELLE":
                      return `/cotisation-exceptionnelle/${op.id}`;
                    case "FONDS_CAISSE":
                      return `/fonds-caisse/${op.id}`;
                    default:
                      return `/operation/${op.id}`;
                  }
                };

                return (
                  <Link key={operation.id} href={getOperationPath(operation)}>
                    <OperationCard
                      operation={operation}
                      onViewDetails={() => {}}
                      onExport={() => handleExport(operation)}
                      onEdit={() => {}}
                    />
                  </Link>
                );
              })}
            </div>
          )}

          {/* Floating Action Button (Mobile Only) */}
          <Button
            onClick={() => setPaymentFormOpen(true)}
            size="icon"
            className="fixed bottom-24 right-6 w-14 h-14 rounded-full shadow-lg md:hidden z-30"
            data-testid="button-fab-payment"
          >
            <Plus className="w-6 h-6" />
          </Button>
        </div>
      </main>

      {/* Bottom Navigation */}
      <BottomNav activeTab={activeTab} onTabChange={handleTabChange} />

      {/* Modals */}
      <QuickPaymentForm
        open={paymentFormOpen}
        onClose={() => setPaymentFormOpen(false)}
        onSubmit={(data) => console.log("Payment submitted:", data)}
        operations={operations.filter(op => op.etat === "EN_COURS").map(op => ({ id: op.id, nom: op.nom }))}
      />

      {selectedOperation && (
        <ExportModal
          open={exportModalOpen}
          onClose={() => setExportModalOpen(false)}
          operationName={selectedOperation.nom}
          onExport={(format) => console.log("Export:", format, selectedOperation.id)}
        />
      )}
    </div>
  );
}
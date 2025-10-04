import { useState } from "react";
import { useRoute, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import PaymentCard, { Payment } from "@/components/PaymentCard";
import OperationBadge from "@/components/OperationBadge";
import StatCard from "@/components/StatCard";
import ProgressBar from "@/components/ProgressBar";
import QuickPaymentForm, { PaymentFormData } from "@/components/QuickPaymentForm";
import ExportModal from "@/components/ExportModal";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { ArrowLeft, Plus, Search, Share2, Edit, AlertCircle } from "lucide-react";
import { format } from "date-fns";
import { fr } from "date-fns/locale";

export default function OperationDetails() {
  const [, params] = useRoute("/operation/:id");
  const [searchQuery, setSearchQuery] = useState("");
  const [paymentFormOpen, setPaymentFormOpen] = useState(false);
  const [exportModalOpen, setExportModalOpen] = useState(false);
  const [selectedPayment, setSelectedPayment] = useState<Payment | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingPayment, setEditingPayment] = useState<Payment | null>(null);
  const [deletingPaymentId, setDeletingPaymentId] = useState<number | null>(null);

  // todo: remove mock functionality - fetch real data
  const mockOperation = {
    id: parseInt(params?.id || "1"),
    nom: "Cotisation Janvier 2025",
    type: "ADHESION" as const,
    montantCible: 50000,
    montantCollecte: 32500,
    dateDebut: new Date(2025, 0, 1),
    dateFin: new Date(2025, 0, 31),
    etat: "EN_COURS" as const,
    nombrePayeurs: 13,
  };

  const mockPayments: Payment[] = [
    {
      id: 1,
      payerName: "Jean Mukendi",
      montant: 2500,
      montantDu: 2500,
      datePaiement: new Date(2025, 0, 15),
      methode: "Mobile Money",
      commentaire: "Orange Money",
    },
    {
      id: 2,
      payerName: "Marie Kabongo",
      montant: 2500,
      montantDu: 2500,
      datePaiement: new Date(2025, 0, 14),
      methode: "Espèces",
    },
    {
      id: 3,
      payerName: "Paul Tshimanga",
      montant: 1500,
      montantDu: 2500,
      datePaiement: new Date(2025, 0, 13),
      methode: "Mobile Money",
      commentaire: "Vodacom M-Pesa - Paiement partiel",
    },
  ];

  const filteredPayments = mockPayments.filter((payment) =>
    payment.payerName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const montantRestant = mockOperation.montantCible - mockOperation.montantCollecte;
  const percentage = (mockOperation.montantCollecte / mockOperation.montantCible) * 100;
  let progressVariant: "primary" | "success" | "warning" = "primary";
  if (percentage >= 100) progressVariant = "success";
  else if (percentage >= 80) progressVariant = "warning";

  const handleEditPayment = (payment: Payment) => {
    setEditingPayment(payment);
    setPaymentFormOpen(true);
  };

  const handleAddPayment = () => {
    setEditingPayment(null);
    setPaymentFormOpen(true);
  };

  const handlePaymentSubmit = (data: PaymentFormData) => {
    if (editingPayment) {
      console.log("Updating payment:", editingPayment.id, data);
      // todo: remove mock functionality - update payment in backend
    } else {
      console.log("Creating payment:", data);
      // todo: remove mock functionality - create payment in backend
      // Replace mock functionality with actual fetch call
      fetch("/api/paiements", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          operationId: data.operationId,
          userId: data.userId, // Use userId from form data
          montant: parseFloat(data.montant.toString()),
          montantDu: data.montantDu ? parseFloat(data.montantDu.toString()) : undefined,
          methodePaiement: data.methode === "mobileMoney" ? "Mobile Money" : data.methode === "especes" ? "Espèces" : "Virement",
          commentaire: data.commentaire,
          datePaiement: data.datePaiement,
        }),
      }).then(response => {
        if (!response.ok) {
          console.error("Failed to create payment");
        }
        // Potentially refetch operations or payments here
      }).catch(error => {
        console.error("Error creating payment:", error);
      });
    }
    setPaymentFormOpen(false);
    setEditingPayment(null);
  };

  const handleDeletePayment = (id: number) => {
    console.log("Deleting payment:", id);
    // todo: remove mock functionality - delete payment from backend
    setDeleteDialogOpen(false);
    setDeletingPaymentId(null);
  };

  const handleClosePaymentForm = () => {
    setPaymentFormOpen(false);
    setEditingPayment(null);
  };

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-card border-b border-border">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center gap-3">
            <Link href="/">
              <Button size="icon" variant="ghost" data-testid="button-back">
                <ArrowLeft className="w-5 h-5" />
              </Button>
            </Link>
            <div className="flex-1 min-w-0">
              <h1 className="text-xl font-bold truncate">{mockOperation.nom}</h1>
            </div>
            <Link href={`/operation/${mockOperation.id}/edit`}>
              <Button size="icon" variant="ghost" data-testid="button-edit">
                <Edit className="w-5 h-5" />
              </Button>
            </Link>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6 max-w-4xl space-y-6">
        {/* Operation Summary Card */}
        <Card>
          <CardHeader className="space-y-3">
            <div className="flex items-center justify-between gap-3">
              <OperationBadge type={mockOperation.type} />
              <span className="text-sm text-muted-foreground">
                {format(mockOperation.dateDebut, "d MMM", { locale: fr })} - {format(mockOperation.dateFin, "d MMM yyyy", { locale: fr })}
              </span>
            </div>
          </CardHeader>
          <CardContent className="space-y-5">
            <div className="grid grid-cols-3 gap-3 sm:gap-4">
              <StatCard label="Ciblé" value={mockOperation.montantCible} />
              <StatCard label="Collecté" value={mockOperation.montantCollecte} variant="success" />
              <StatCard label="Restant" value={montantRestant} variant={montantRestant > 0 ? "warning" : "success"} />
            </div>

            <ProgressBar
              current={mockOperation.montantCollecte}
              target={mockOperation.montantCible}
              variant={progressVariant}
            />

            <div className="flex items-center justify-between pt-2">
              <div className="flex items-center gap-2 text-sm sm:text-base">
                <span className="font-semibold text-foreground">{mockOperation.nombrePayeurs}</span>
                <span className="text-muted-foreground">payeur{mockOperation.nombrePayeurs > 1 ? "s" : ""}</span>
              </div>
              <Button onClick={() => setExportModalOpen(true)} variant="outline" data-testid="button-share">
                <Share2 className="w-4 h-4 mr-2" />
                Partager
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Payments Section */}
        <Card>
          <CardHeader>
            <CardTitle>Liste des paiements</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex gap-3">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Rechercher un payeur..."
                  className="pl-10"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  data-testid="input-search-payments"
                />
              </div>
              <Button onClick={handleAddPayment} data-testid="button-add-payment">
                <Plus className="w-4 h-4 mr-2" />
                Ajouter
              </Button>
            </div>

            {filteredPayments.length === 0 ? (
              <div className="text-center py-8 space-y-2">
                <AlertCircle className="w-8 h-8 mx-auto text-muted-foreground" />
                <p className="text-muted-foreground">Aucun paiement trouvé</p>
              </div>
            ) : (
              <div className="space-y-3">
                {filteredPayments.map((payment) => (
                  <PaymentCard
                    key={payment.id}
                    payment={payment}
                    onClick={() => handleEditPayment(payment)}
                    onDelete={() => setDeletingPaymentId(payment.id)}
                  />
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </main>

      {/* Modals */}
      <QuickPaymentForm
        open={paymentFormOpen}
        onClose={handleClosePaymentForm}
        onSubmit={handlePaymentSubmit}
        operations={[{ id: mockOperation.id, nom: mockOperation.nom }]}
        mode={editingPayment ? "edit" : "create"}
        initialData={editingPayment ? {
          id: editingPayment.id,
          operationId: mockOperation.id,
          payerName: editingPayment.payerName,
          montant: editingPayment.montant,
          datePaiement: format(editingPayment.datePaiement, "yyyy-MM-dd"),
          methode: editingPayment.methode.toLowerCase(),
          commentaire: editingPayment.commentaire || "",
          userId: 1 // Assuming a default or contextually derived userId for editing
        } : undefined}
      />

      <ExportModal
        open={exportModalOpen}
        onClose={() => setExportModalOpen(false)}
        operationName={mockOperation.nom}
        onExport={(format) => console.log("Export:", format)}
      />

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deletingPaymentId !== null} onOpenChange={(open) => !open && setDeletingPaymentId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmer la suppression</AlertDialogTitle>
            <AlertDialogDescription>
              Êtes-vous sûr de vouloir supprimer ce paiement de {mockPayments.find(p => p.id === deletingPaymentId)?.payerName} ? Cette action est irréversible.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel data-testid="button-cancel-delete">Annuler</AlertDialogCancel>
            <AlertDialogAction onClick={() => deletingPaymentId && handleDeletePayment(deletingPaymentId)} className="bg-red-500 hover:bg-red-600" data-testid="button-confirm-delete">
              Supprimer
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
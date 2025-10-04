import { useState, useEffect } from "react"; // Import useEffect
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { X } from "lucide-react";

interface QuickPaymentFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: PaymentFormData) => void;
  operations?: { id: number; nom: string }[]; // Made operations optional
  initialData?: Partial<PaymentFormData>; // Use Partial for initialData
  mode?: "create" | "edit";
  onDelete?: () => void;
}

export interface PaymentFormData {
  id?: number;
  operationId: number;
  payerName: string;
  userId?: number; // Added userId
  montant: number;
  montantDu?: number;
  datePaiement: string;
  methode: string;
  commentaire: string;
}

export default function QuickPaymentForm({
  open,
  onClose,
  onSubmit,
  operations = [],
  initialData,
  mode = "create",
  onDelete
}: QuickPaymentFormProps) {
  const defaultFormData: PaymentFormData = {
    operationId: operations.length > 0 ? operations[0].id : 0, // Set default to first operation if available
    payerName: "",
    userId: undefined, // Initialize userId to undefined
    montant: 0,
    datePaiement: new Date().toISOString().split('T')[0],
    methode: "especes",
    commentaire: "",
  };

  const [formData, setFormData] = useState<PaymentFormData>(() => {
    if (initialData) {
      return { ...defaultFormData, ...initialData };
    }
    return defaultFormData;
  });

  const [users, setUsers] = useState<Array<{ id: number; nom: string }>>([]);

  // Effect to update form data when initialData changes or when the dialog opens/closes
  useEffect(() => {
    if (open) {
      fetchUsers(); // Fetch users when the dialog opens
      if (initialData) {
        setFormData({ ...defaultFormData, ...initialData });
      } else {
        setFormData(defaultFormData);
      }
    }
  }, [open, initialData, operations]); // Depend on open, initialData, and operations

  const fetchUsers = async () => {
    try {
      const response = await fetch("/api/users");
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        console.error("Failed to fetch users:", response.statusText);
      }
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };


  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
    if (mode === "create") {
      setFormData(defaultFormData); // Reset form for new entry
    }
    // onClose will be called by the parent component after onSubmit is handled
  };

  const handleClose = () => {
    // Reset form to default or initialData when closing if not editing
    if (mode === "create") {
      setFormData(defaultFormData);
    }
    onClose();
  };


  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-2xl">
            {mode === "edit" ? "Modifier le paiement" : "Enregistrer un paiement"}
          </DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <Label htmlFor="operation">Opération *</Label>
            <Select
              value={formData.operationId.toString()}
              onValueChange={(value) => setFormData({ ...formData, operationId: parseInt(value) })}
              required
              disabled={mode === "edit"} // Disable operation selection when editing
            >
              <SelectTrigger id="operation" data-testid="select-operation">
                <SelectValue placeholder="Sélectionner une opération" />
              </SelectTrigger>
              <SelectContent>
                {operations.map((op) => (
                  <SelectItem key={op.id} value={op.id.toString()}>
                    {op.nom}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="userId">Payeur *</Label>
            <Select
              value={formData.userId?.toString() || ""}
              onValueChange={(value) => {
                const userId = parseInt(value);
                const user = users.find(u => u.id === userId);
                setFormData({
                  ...formData,
                  userId,
                  payerName: user?.nom || "" // Set payerName based on selected user
                });
              }}
              required
              disabled={mode === "edit"} // Disable user selection when editing if appropriate
            >
              <SelectTrigger id="userId" data-testid="select-user">
                <SelectValue placeholder="Sélectionner un payeur" />
              </SelectTrigger>
              <SelectContent>
                {users.map((user) => (
                  <SelectItem key={user.id} value={user.id.toString()}>
                    {user.nom}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="montantDu">Montant dû (XAF)</Label>
              <Input
                id="montantDu"
                type="number"
                value={formData.montantDu || ""}
                onChange={(e) => setFormData({ ...formData, montantDu: parseFloat(e.target.value) || undefined })}
                placeholder="2500"
                min="0"
                step="100"
                data-testid="input-amount-due"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="montant">Montant payé (XAF) *</Label>
              <Input
                id="montant"
                type="number"
                value={formData.montant || ""}
                onChange={(e) => setFormData({ ...formData, montant: parseFloat(e.target.value) || 0 })}
                placeholder="2500"
                required
                min="0"
                step="100"
                data-testid="input-amount"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="date">Date</Label>
              <Input
                id="date"
                type="date"
                value={formData.datePaiement}
                onChange={(e) => setFormData({ ...formData, datePaiement: e.target.value })}
                data-testid="input-date"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="methode">Méthode</Label>
              <Select
                value={formData.methode}
                onValueChange={(value) => setFormData({ ...formData, methode: value })}
              >
                <SelectTrigger id="methode" data-testid="select-method">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="especes">Espèces</SelectItem>
                  <SelectItem value="mobile money">Mobile Money</SelectItem>
                  <SelectItem value="carte">Carte</SelectItem>
                  <SelectItem value="autre">Autre</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="commentaire">Commentaire (optionnel)</Label>
            <Textarea
              id="commentaire"
              value={formData.commentaire}
              onChange={(e) => setFormData({ ...formData, commentaire: e.target.value })}
              placeholder="Note ou référence du reçu..."
              rows={2}
              data-testid="input-comment"
            />
          </div>

          <div className="flex flex-col gap-3 pt-4">
            <div className="flex gap-3">
              <Button type="button" variant="outline" onClick={handleClose} className="flex-1" data-testid="button-cancel">
                Annuler
              </Button>
              <Button type="submit" className="flex-1" data-testid="button-save-payment">
                {mode === "edit" ? "Modifier" : "Enregistrer"}
              </Button>
            </div>
            {mode === "edit" && onDelete && (
              <Button type="button" variant="destructive" onClick={onDelete} className="w-full" data-testid="button-delete-payment">
                Supprimer le paiement
              </Button>
            )}
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
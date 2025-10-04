import { useState } from "react";
import { useRoute, useLocation } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { ArrowLeft, Save, Trash2 } from "lucide-react";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

export default function EditOperation() {
  const [, params] = useRoute("/operation/:id/edit");
  const [, setLocation] = useLocation();

  // todo: remove mock functionality - fetch real data
  const [formData, setFormData] = useState({
    nom: "Cotisation Janvier 2025",
    type: "ADHESION",
    montantCible: 50000,
    dateDebut: "2025-01-01",
    dateFin: "2025-01-31",
    description: "",
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Updating operation:", formData);
    setLocation(`/operation/${params?.id}`);
  };

  const handleDelete = () => {
    console.log("Deleting operation:", params?.id);
    setLocation("/");
  };

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-card border-b border-border">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center gap-3">
            <Button size="icon" variant="ghost" onClick={() => setLocation(`/operation/${params?.id}`)} data-testid="button-back">
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="flex-1">
              <h1 className="text-xl font-bold">Modifier l'opération</h1>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6 max-w-2xl">
        <Card>
          <CardHeader>
            <CardTitle>Informations de l'opération</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="nom">Nom de l'opération *</Label>
                <Input
                  id="nom"
                  value={formData.nom}
                  onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                  placeholder="Ex: Cotisation Janvier 2025"
                  required
                  data-testid="input-operation-name"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="type">Type d'opération *</Label>
                <Select
                  value={formData.type}
                  onValueChange={(value) => setFormData({ ...formData, type: value })}
                  required
                >
                  <SelectTrigger id="type" data-testid="select-operation-type">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ADHESION">Adhésion</SelectItem>
                    <SelectItem value="FONDS_CAISSE">Fonds de caisse</SelectItem>
                    <SelectItem value="COTISATION_EXCEPTIONNELLE">Cotisation exceptionnelle</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="montant">Montant cible (XAF) *</Label>
                <Input
                  id="montant"
                  type="number"
                  value={formData.montantCible}
                  onChange={(e) => setFormData({ ...formData, montantCible: parseFloat(e.target.value) || 0 })}
                  placeholder="50000"
                  required
                  min="0"
                  step="100"
                  data-testid="input-target-amount"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="dateDebut">Date de début *</Label>
                  <Input
                    id="dateDebut"
                    type="date"
                    value={formData.dateDebut}
                    onChange={(e) => setFormData({ ...formData, dateDebut: e.target.value })}
                    required
                    data-testid="input-start-date"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="dateFin">Date de fin *</Label>
                  <Input
                    id="dateFin"
                    type="date"
                    value={formData.dateFin}
                    onChange={(e) => setFormData({ ...formData, dateFin: e.target.value })}
                    required
                    data-testid="input-end-date"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="description">Description (optionnel)</Label>
                <Textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Détails supplémentaires sur cette opération..."
                  rows={3}
                  data-testid="input-description"
                />
              </div>

              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <Button type="submit" className="flex-1" data-testid="button-save">
                  <Save className="w-4 h-4 mr-2" />
                  Enregistrer
                </Button>

                <AlertDialog>
                  <AlertDialogTrigger asChild>
                    <Button type="button" variant="destructive" className="flex-1" data-testid="button-delete">
                      <Trash2 className="w-4 h-4 mr-2" />
                      Supprimer
                    </Button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>Confirmer la suppression</AlertDialogTitle>
                      <AlertDialogDescription>
                        Êtes-vous sûr de vouloir supprimer cette opération ? Cette action est irréversible et supprimera également tous les paiements associés.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel data-testid="button-cancel-delete">Annuler</AlertDialogCancel>
                      <AlertDialogAction onClick={handleDelete} data-testid="button-confirm-delete">
                        Supprimer
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </div>
            </form>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}

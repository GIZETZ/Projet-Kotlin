import { useState } from "react";
import { useLocation } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { ArrowLeft, Plus } from "lucide-react";

export default function NewOperation() {
  const [, setLocation] = useLocation();

  const [formData, setFormData] = useState({
    nom: "",
    type: "",
    montantCible: 0,
    dateDebut: new Date().toISOString().split('T')[0],
    dateFin: "",
    description: "",
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Creating operation:", formData);
    // todo: remove mock functionality - save to backend
    setLocation("/");
  };

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-card border-b border-border">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center gap-3">
            <Button size="icon" variant="ghost" onClick={() => setLocation("/")} data-testid="button-back">
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="flex-1">
              <h1 className="text-xl font-bold">Nouvelle opération</h1>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6 max-w-2xl">
        <Card>
          <CardHeader>
            <CardTitle>Créer une opération</CardTitle>
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
                    <SelectValue placeholder="Sélectionner un type" />
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
                  value={formData.montantCible || ""}
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

              <div className="flex gap-3 pt-4">
                <Button type="button" variant="outline" onClick={() => setLocation("/")} className="flex-1" data-testid="button-cancel">
                  Annuler
                </Button>
                <Button type="submit" className="flex-1" data-testid="button-create">
                  <Plus className="w-4 h-4 mr-2" />
                  Créer
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}

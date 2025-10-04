
import { useState } from "react";
import { Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ArrowLeft, Save } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

export default function AdhesionSettings() {
  const { toast } = useToast();
  const [montantAdhesion, setMontantAdhesion] = useState(2500);
  const [isSaving, setIsSaving] = useState(false);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);

    // todo: remove mock functionality - save to backend
    setTimeout(() => {
      toast({
        title: "Paramètres enregistrés",
        description: "Le montant d'adhésion a été mis à jour avec succès.",
      });
      setIsSaving(false);
    }, 500);
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
              <h1 className="text-xl font-bold truncate">Paramètres d'adhésion</h1>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6 max-w-2xl">
        <Card>
          <CardHeader>
            <CardTitle>Configuration du montant d'adhésion</CardTitle>
            <CardDescription>
              Définissez le montant que chaque personne doit payer pour adhérer
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSave} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="montantAdhesion">Montant d'adhésion (XAF) *</Label>
                <Input
                  id="montantAdhesion"
                  type="number"
                  value={montantAdhesion}
                  onChange={(e) => setMontantAdhesion(parseFloat(e.target.value) || 0)}
                  placeholder="2500"
                  required
                  min="0"
                  step="100"
                  data-testid="input-montant-adhesion"
                />
                <p className="text-sm text-muted-foreground">
                  Ce montant sera utilisé comme référence pour tous les paiements d'adhésion
                </p>
              </div>

              <div className="bg-muted p-4 rounded-lg space-y-2">
                <h3 className="font-semibold">Aperçu</h3>
                <p className="text-sm text-muted-foreground">
                  Montant configuré: <span className="font-bold text-foreground">
                    {new Intl.NumberFormat("fr-FR", { style: "currency", currency: "XAF" }).format(montantAdhesion)}
                  </span>
                </p>
                <p className="text-sm text-muted-foreground">
                  Ce montant s'appliquera automatiquement aux nouvelles opérations d'adhésion.
                </p>
              </div>

              <div className="flex gap-3 pt-4">
                <Link href="/">
                  <Button type="button" variant="outline" className="flex-1" data-testid="button-cancel">
                    Annuler
                  </Button>
                </Link>
                <Button type="submit" disabled={isSaving} className="flex-1" data-testid="button-save">
                  <Save className="w-4 h-4 mr-2" />
                  {isSaving ? "Enregistrement..." : "Enregistrer"}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}

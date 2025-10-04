import { useState, useEffect } from "react";
import { useLocation, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ArrowLeft, Save } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

export default function AdhesionSettings() {
  const { toast } = useToast();
  const [, setLocation] = useLocation();
  const [montantAdhesion, setMontantAdhesion] = useState(2500);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    fetchMontantAdhesion();
  }, []);

  const fetchMontantAdhesion = async () => {
    try {
      const response = await fetch("/api/parametres/montant_adhesion");
      if (response.ok) {
        const data = await response.json();
        setMontantAdhesion(parseFloat(data.valeur));
      } else {
        // Handle error, e.g., show a toast
        toast({
          title: "Erreur",
          description: "Impossible de charger le montant d'adhésion.",
          variant: "destructive",
        });
      }
    } catch (error) {
      console.error("Error fetching montant adhesion:", error);
      toast({
        title: "Erreur de connexion",
        description: "Impossible de se connecter au serveur pour charger le montant d'adhésion.",
        variant: "destructive",
      });
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);

    try {
      const response = await fetch("/api/parametres/montant_adhesion", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ valeur: montantAdhesion.toString() }),
      });

      if (response.ok) {
        toast({
          title: "Paramètres enregistrés",
          description: "Le montant d'adhésion a été mis à jour avec succès.",
        });
        setLocation("/");
      } else {
        const errorData = await response.json();
        toast({
          title: "Erreur lors de la sauvegarde",
          description: errorData.message || "Une erreur est survenue. Veuillez réessayer.",
          variant: "destructive",
        });
      }
    } catch (error) {
      console.error("Error saving setting:", error);
      toast({
        title: "Erreur de connexion",
        description: "Impossible de se connecter au serveur pour sauvegarder les paramètres.",
        variant: "destructive",
      });
    } finally {
      setIsSaving(false);
    }
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
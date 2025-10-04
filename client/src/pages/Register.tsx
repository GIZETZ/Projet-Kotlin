
import { useState } from "react";
import { useLocation } from "wouter";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { User, Mail, Phone, Building, Lock } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

export default function Register() {
  const [, setLocation] = useLocation();
  const { toast } = useToast();
  const [formData, setFormData] = useState({
    nom: "",
    email: "",
    telephone: "",
    organisation: "",
    pin: "",
    confirmPin: "",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (formData.pin !== formData.confirmPin) {
      toast({
        title: "Erreur",
        description: "Les codes PIN ne correspondent pas",
        variant: "destructive",
      });
      return;
    }

    if (formData.pin.length !== 4) {
      toast({
        title: "Erreur",
        description: "Le code PIN doit contenir 4 chiffres",
        variant: "destructive",
      });
      return;
    }

    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nom: formData.nom,
          email: formData.email,
          telephone: formData.telephone,
          organisation: formData.organisation,
          pin: formData.pin,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        toast({
          title: "Succès",
          description: "Votre compte a été créé avec succès",
        });
        setLocation("/login");
      } else {
        toast({
          title: "Erreur",
          description: data.message || "Erreur lors de la création du compte",
          variant: "destructive",
        });
      }
    } catch (error) {
      toast({
        title: "Erreur",
        description: "Erreur de connexion au serveur",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-2">
          <CardTitle className="text-2xl text-center">Créer un compte</CardTitle>
          <CardDescription className="text-center">
            Remplissez le formulaire pour vous inscrire
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="nom" className="flex items-center gap-2">
                <User className="w-4 h-4" />
                Nom complet
              </Label>
              <Input
                id="nom"
                required
                value={formData.nom}
                onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                placeholder="Jean Mukendi"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="email" className="flex items-center gap-2">
                <Mail className="w-4 h-4" />
                Email
              </Label>
              <Input
                id="email"
                type="email"
                required
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="jean@example.com"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="telephone" className="flex items-center gap-2">
                <Phone className="w-4 h-4" />
                Téléphone (optionnel)
              </Label>
              <Input
                id="telephone"
                type="tel"
                value={formData.telephone}
                onChange={(e) => setFormData({ ...formData, telephone: e.target.value })}
                placeholder="+243 900 000 000"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="organisation" className="flex items-center gap-2">
                <Building className="w-4 h-4" />
                Organisation (optionnel)
              </Label>
              <Input
                id="organisation"
                value={formData.organisation}
                onChange={(e) => setFormData({ ...formData, organisation: e.target.value })}
                placeholder="Association Musep50"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="pin" className="flex items-center gap-2">
                <Lock className="w-4 h-4" />
                Code PIN (4 chiffres)
              </Label>
              <Input
                id="pin"
                type="password"
                inputMode="numeric"
                maxLength={4}
                required
                value={formData.pin}
                onChange={(e) => setFormData({ ...formData, pin: e.target.value.replace(/\D/g, '') })}
                placeholder="••••"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPin">Confirmer le code PIN</Label>
              <Input
                id="confirmPin"
                type="password"
                inputMode="numeric"
                maxLength={4}
                required
                value={formData.confirmPin}
                onChange={(e) => setFormData({ ...formData, confirmPin: e.target.value.replace(/\D/g, '') })}
                placeholder="••••"
              />
            </div>

            <Button type="submit" className="w-full">
              Créer mon compte
            </Button>

            <div className="text-center">
              <Button
                type="button"
                variant="link"
                onClick={() => setLocation("/login")}
                className="text-sm"
              >
                Vous avez déjà un compte ? Se connecter
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

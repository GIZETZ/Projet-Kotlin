
import { useState } from "react";
import { useLocation } from "wouter";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Lock, Mail, ArrowRight } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface LoginProps {
  onLoginSuccess: (user: any) => void;
}

export default function Login({ onLoginSuccess }: LoginProps) {
  const [, setLocation] = useLocation();
  const { toast } = useToast();
  const [step, setStep] = useState<'email' | 'pin'>('email');
  const [email, setEmail] = useState("");
  const [pin, setPin] = useState("");

  const handleEmailSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email) {
      setStep('pin');
    }
  };

  const handlePinSubmit = async (pinToSubmit?: string) => {
    const finalPin = pinToSubmit || pin;

    if (finalPin.length !== 4) {
      toast({
        title: "Erreur",
        description: "Le code PIN doit contenir 4 chiffres",
        variant: "destructive",
      });
      return;
    }

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, pin: finalPin }),
      });

      const data = await response.json();

      if (response.ok) {
        toast({
          title: "Connexion réussie",
          description: `Bienvenue ${data.user.nom}`,
        });
        onLoginSuccess(data.user);
      } else {
        toast({
          title: "Erreur",
          description: data.message || "Email ou PIN incorrect",
          variant: "destructive",
        });
        setPin("");
      }
    } catch (error) {
      toast({
        title: "Erreur",
        description: "Erreur de connexion au serveur",
        variant: "destructive",
      });
    }
  };

  const handleNumberClick = (num: number) => {
    if (pin.length < 4) {
      const newPin = pin + num;
      setPin(newPin);
      
      if (newPin.length === 4) {
        setTimeout(() => {
          handlePinSubmit(newPin);
        }, 200);
      }
    }
  };

  const handleDelete = () => {
    setPin(pin.slice(0, -1));
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="space-y-4 text-center">
          <div className="mx-auto w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center">
            {step === 'email' ? <Mail className="w-6 h-6 text-primary" /> : <Lock className="w-6 h-6 text-primary" />}
          </div>
          <CardTitle className="text-2xl">
            {step === 'email' ? 'Connexion' : 'Code PIN'}
          </CardTitle>
          <CardDescription>
            {step === 'email' ? 'Entrez votre adresse email' : 'Entrez votre code PIN'}
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {step === 'email' ? (
            <form onSubmit={handleEmailSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="jean@example.com"
                  autoFocus
                />
              </div>
              <Button type="submit" className="w-full">
                Continuer
                <ArrowRight className="w-4 h-4 ml-2" />
              </Button>
              <div className="text-center">
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => setLocation("/register")}
                  className="text-sm"
                >
                  Pas de compte ? S'inscrire
                </Button>
              </div>
            </form>
          ) : (
            <div className="space-y-4">
              <div className="flex justify-center gap-3 mb-6">
                {Array.from({ length: 4 }).map((_, i) => (
                  <div
                    key={i}
                    className={`w-3 h-3 rounded-full transition-all ${
                      i < pin.length ? "bg-primary" : "bg-muted"
                    }`}
                  />
                ))}
              </div>

              <div className="grid grid-cols-3 gap-3">
                {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                  <Button
                    key={num}
                    onClick={() => handleNumberClick(num)}
                    variant="outline"
                    size="lg"
                    className="h-14 text-xl font-semibold"
                  >
                    {num}
                  </Button>
                ))}
                <Button
                  onClick={() => setStep('email')}
                  variant="ghost"
                  size="lg"
                  className="h-14 text-sm"
                >
                  Retour
                </Button>
                <Button
                  onClick={() => handleNumberClick(0)}
                  variant="outline"
                  size="lg"
                  className="h-14 text-xl font-semibold"
                >
                  0
                </Button>
                <Button
                  onClick={handleDelete}
                  variant="ghost"
                  size="lg"
                  className="h-14"
                  disabled={pin.length === 0}
                >
                  ⌫
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

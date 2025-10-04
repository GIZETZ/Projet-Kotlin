import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Lock, Delete } from "lucide-react";

interface PINEntryProps {
  onSubmit: (pin: string) => void;
  title?: string;
}

export default function PINEntry({ onSubmit, title = "Code PIN" }: PINEntryProps) {
  const [pin, setPin] = useState("");
  const pinLength = 4;

  const handleNumberClick = (num: number) => {
    if (pin.length < pinLength) {
      const newPin = pin + num;
      setPin(newPin);
      if (newPin.length === pinLength) {
        setTimeout(() => {
          onSubmit(newPin);
          setPin("");
        }, 100);
      }
    }
  };

  const handleDelete = () => {
    setPin(pin.slice(0, -1));
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-background p-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="space-y-4 text-center">
          <div className="mx-auto w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center">
            <Lock className="w-6 h-6 text-primary" />
          </div>
          <CardTitle className="text-2xl">{title}</CardTitle>
          <div className="flex justify-center gap-3">
            {Array.from({ length: pinLength }).map((_, i) => (
              <div
                key={i}
                className={`w-3 h-3 rounded-full transition-all ${
                  i < pin.length ? "bg-primary" : "bg-muted"
                }`}
                data-testid={`pin-dot-${i}`}
              />
            ))}
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-3 gap-3">
            {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
              <Button
                key={num}
                onClick={() => handleNumberClick(num)}
                variant="outline"
                size="lg"
                className="h-14 text-xl font-semibold"
                data-testid={`button-pin-${num}`}
              >
                {num}
              </Button>
            ))}
            <div />
            <Button
              onClick={() => handleNumberClick(0)}
              variant="outline"
              size="lg"
              className="h-14 text-xl font-semibold"
              data-testid="button-pin-0"
            >
              0
            </Button>
            <Button
              onClick={handleDelete}
              variant="ghost"
              size="lg"
              className="h-14"
              disabled={pin.length === 0}
              data-testid="button-pin-delete"
            >
              <Delete className="w-5 h-5" />
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

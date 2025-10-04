import { useState } from 'react';
import QuickPaymentForm from '../QuickPaymentForm';
import { Button } from '@/components/ui/button';

export default function QuickPaymentFormExample() {
  const [open, setOpen] = useState(false);

  const mockOperations = [
    { id: 1, nom: "Cotisation Janvier 2025" },
    { id: 2, nom: "Fonds de caisse Q1" },
  ];

  return (
    <div className="p-6">
      <Button onClick={() => setOpen(true)}>Ouvrir le formulaire</Button>
      <QuickPaymentForm
        open={open}
        onClose={() => setOpen(false)}
        onSubmit={(data) => console.log('Payment submitted:', data)}
        operations={mockOperations}
      />
    </div>
  );
}

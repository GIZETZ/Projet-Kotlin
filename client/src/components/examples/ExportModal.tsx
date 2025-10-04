import { useState } from 'react';
import ExportModal from '../ExportModal';
import { Button } from '@/components/ui/button';

export default function ExportModalExample() {
  const [open, setOpen] = useState(false);

  return (
    <div className="p-6">
      <Button onClick={() => setOpen(true)}>Ouvrir Export</Button>
      <ExportModal
        open={open}
        onClose={() => setOpen(false)}
        operationName="Cotisation Janvier 2025"
        onExport={(format) => console.log('Export format:', format)}
      />
    </div>
  );
}

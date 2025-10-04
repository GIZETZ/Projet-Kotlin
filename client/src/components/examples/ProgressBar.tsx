import ProgressBar from '../ProgressBar';

export default function ProgressBarExample() {
  return (
    <div className="space-y-6 p-6 max-w-md">
      <ProgressBar current={15000} target={50000} variant="primary" />
      <ProgressBar current={42000} target={50000} variant="warning" />
      <ProgressBar current={50000} target={50000} variant="success" />
    </div>
  );
}

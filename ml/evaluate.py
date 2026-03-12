from pathlib import Path

import joblib
import pandas as pd
from sklearn.metrics import accuracy_score, f1_score, precision_score, recall_score


# Resolve all paths from the repository root.
BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "outputs"
MODEL_PATH = OUTPUT_DIR / "final_model.pkl"
TEST_SPLIT_PATH = OUTPUT_DIR / "test_split.csv"
METRICS_PATH = OUTPUT_DIR / "metrics.txt"
ERROR_ANALYSIS_PATH = OUTPUT_DIR / "error_analysis.txt"

# Use the exact same features as in training.
FEATURE_COLUMNS = [
    "enrollment_year",
    "num_modules",
    "num_marks",
    "min_score",
    "max_score",
    "num_reports",
]
TARGET_COLUMN = "at_risk"


def main():
    # Create the outputs folder if it does not already exist.
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    # Load the saved model and the held-out test split created during training.
    model = joblib.load(MODEL_PATH)
    df = pd.read_csv(TEST_SPLIT_PATH)

    X = df[FEATURE_COLUMNS]
    y = df[TARGET_COLUMN]

    # Predict using the saved model.
    y_pred = model.predict(X)

    # Compute evaluation metrics.
    accuracy = accuracy_score(y, y_pred)
    precision = precision_score(y, y_pred, zero_division=0)
    recall = recall_score(y, y_pred, zero_division=0)
    f1 = f1_score(y, y_pred, zero_division=0)

    # Write a single-line metrics log.
    metrics_line = (
        f"val_accuracy={accuracy:.2f} "
        f"val_precision={precision:.2f} "
        f"val_recall={recall:.2f} "
        f"val_f1={f1:.2f} "
        f"checkpoint=outputs/final_model.pkl"
    )
    METRICS_PATH.write_text(metrics_line, encoding="utf-8")

    # Build a simple error analysis summary.
    results_df = df.copy()
    results_df["predicted_at_risk"] = y_pred
    misclassified = results_df[results_df[TARGET_COLUMN] != results_df["predicted_at_risk"]]

    if misclassified.empty:
        error_summary = (
            "Error analysis summary\n"
            "There are no misclassified examples in this evaluation run.\n"
            "All rows were predicted correctly.\n"
        )
    else:
        example = misclassified.iloc[0]
        example_parts = [
            f"actual at_risk={example[TARGET_COLUMN]}",
            f"predicted at_risk={example['predicted_at_risk']}",
            f"enrollment_year={example['enrollment_year']}",
            f"num_modules={example['num_modules']}",
            f"num_marks={example['num_marks']}",
            f"min_score={example['min_score']}",
            f"max_score={example['max_score']}",
            f"num_reports={example['num_reports']}",
        ]

        if "student_id" in example.index:
            example_parts.insert(0, f"student_id={example['student_id']}")
        if "avg_score" in example.index:
            example_parts.append(f"avg_score={example['avg_score']}")

        error_summary = (
            "Error analysis summary\n"
            f"Total misclassified rows: {len(misclassified)}\n"
            "Example misclassification: "
            + ", ".join(example_parts)
            + "\n"
        )

    ERROR_ANALYSIS_PATH.write_text(error_summary, encoding="utf-8")

    print(metrics_line)
    print(f"Metrics saved to: {METRICS_PATH}")
    print(f"Error analysis saved to: {ERROR_ANALYSIS_PATH}")


if __name__ == "__main__":
    main()

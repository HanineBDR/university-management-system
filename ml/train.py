import random
from pathlib import Path

import joblib
import numpy as np
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, f1_score, precision_score, recall_score
from sklearn.model_selection import train_test_split


# Set seeds so the train/test split and model behavior are reproducible.
random.seed(42)
np.random.seed(42)

# Resolve all paths from the repository root.
BASE_DIR = Path(__file__).resolve().parent.parent
DATA_PATH = BASE_DIR / "data" / "student_risk_dataset.csv"
OUTPUT_DIR = BASE_DIR / "outputs"
MODEL_PATH = OUTPUT_DIR / "final_model.pkl"
TEST_SPLIT_PATH = OUTPUT_DIR / "test_split.csv"

# Use only features that do not leak the target.
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

    # Load the dataset created by ml/build_dataset.py.
    df = pd.read_csv(DATA_PATH)

    # Separate input features and target column.
    X = df[FEATURE_COLUMNS]
    y = df[TARGET_COLUMN]

    # Split the data into training and test sets.
    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=0.2,
        random_state=42,
        stratify=y,
    )

    # Logistic Regression is simple and appropriate for binary classification.
    # max_iter is increased to avoid convergence problems.
    model = LogisticRegression(max_iter=1000, random_state=42)
    model.fit(X_train, y_train)

    # Save the held-out test split so evaluation can use only unseen rows.
    test_split_df = X_test.copy()
    test_split_df[TARGET_COLUMN] = y_test.values

    # Keep extra columns that help with interpretation if they exist in the dataset.
    if "student_id" in df.columns:
        test_split_df["student_id"] = df.loc[X_test.index, "student_id"].values
    if "avg_score" in df.columns:
        test_split_df["avg_score"] = df.loc[X_test.index, "avg_score"].values

    # Reorder columns to keep the saved file easy to read.
    saved_columns = FEATURE_COLUMNS.copy()
    saved_columns.append(TARGET_COLUMN)
    if "student_id" in test_split_df.columns:
        saved_columns.append("student_id")
    if "avg_score" in test_split_df.columns:
        saved_columns.append("avg_score")
    test_split_df = test_split_df[saved_columns]
    test_split_df.to_csv(TEST_SPLIT_PATH, index=False)

    # Predict on the test set and compute basic classification metrics.
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(y_test, y_pred, zero_division=0)
    recall = recall_score(y_test, y_pred, zero_division=0)
    f1 = f1_score(y_test, y_pred, zero_division=0)

    # Save the trained model for reuse in evaluation.
    joblib.dump(model, MODEL_PATH)

    print(f"Model saved to: {MODEL_PATH}")
    print(f"Test split saved to: {TEST_SPLIT_PATH}")
    print(f"accuracy={accuracy:.4f}")
    print(f"precision={precision:.4f}")
    print(f"recall={recall:.4f}")
    print(f"f1={f1:.4f}")


if __name__ == "__main__":
    main()
